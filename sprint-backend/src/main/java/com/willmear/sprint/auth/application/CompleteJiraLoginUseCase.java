package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.api.AuthService.LoginResult;
import com.willmear.sprint.auth.domain.AppUser;
import com.willmear.sprint.auth.domain.AuthLoginState;
import com.willmear.sprint.auth.domain.AuthProvider;
import com.willmear.sprint.auth.domain.AuthenticatedSession;
import com.willmear.sprint.auth.entity.AppSessionEntity;
import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.auth.mapper.AppSessionMapper;
import com.willmear.sprint.auth.mapper.AppUserMapper;
import com.willmear.sprint.auth.mapper.AuthLoginStateMapper;
import com.willmear.sprint.auth.repository.AppSessionRepository;
import com.willmear.sprint.auth.repository.AppUserRepository;
import com.willmear.sprint.auth.repository.AuthLoginStateRepository;
import com.willmear.sprint.auth.security.AuthProperties;
import com.willmear.sprint.auth.security.SessionTokenHasher;
import com.willmear.sprint.common.exception.InvalidOAuthStateException;
import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CompleteJiraLoginUseCase {

    private final AuthLoginStateRepository authLoginStateRepository;
    private final AuthLoginStateMapper authLoginStateMapper;
    private final JiraOAuthClientPort jiraOAuthClientPort;
    private final JiraClientPort jiraClientPort;
    private final AppUserRepository appUserRepository;
    private final AppSessionRepository appSessionRepository;
    private final AppUserMapper appUserMapper;
    private final AppSessionMapper appSessionMapper;
    private final SessionTokenHasher sessionTokenHasher;
    private final AuthProperties authProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public CompleteJiraLoginUseCase(
            AuthLoginStateRepository authLoginStateRepository,
            AuthLoginStateMapper authLoginStateMapper,
            JiraOAuthClientPort jiraOAuthClientPort,
            JiraClientPort jiraClientPort,
            AppUserRepository appUserRepository,
            AppSessionRepository appSessionRepository,
            AppUserMapper appUserMapper,
            AppSessionMapper appSessionMapper,
            SessionTokenHasher sessionTokenHasher,
            AuthProperties authProperties
    ) {
        this.authLoginStateRepository = authLoginStateRepository;
        this.authLoginStateMapper = authLoginStateMapper;
        this.jiraOAuthClientPort = jiraOAuthClientPort;
        this.jiraClientPort = jiraClientPort;
        this.appUserRepository = appUserRepository;
        this.appSessionRepository = appSessionRepository;
        this.appUserMapper = appUserMapper;
        this.appSessionMapper = appSessionMapper;
        this.sessionTokenHasher = sessionTokenHasher;
        this.authProperties = authProperties;
    }

    @Transactional
    public LoginResult complete(String code, String state) {
        AuthLoginState loginState = getActiveState(state);
        try {
            JiraOAuthTokenResponse tokens = jiraOAuthClientPort.exchangeCodeForTokens(code, authProperties.jiraLoginRedirectUri());
            JiraAccessibleResource resource = jiraOAuthClientPort.getAccessibleResources(tokens.accessToken()).stream()
                    .min(Comparator.comparing(item -> normalizeUrl(item.url()) == null ? "" : normalizeUrl(item.url())))
                    .orElseThrow(() -> new InvalidOAuthStateException(state));
            JiraAccountSummary accountSummary = jiraClientPort.getCurrentAccount(new JiraConnection(
                    null,
                    null,
                    resource.url(),
                    JiraAuthType.OAUTH,
                    JiraConnectionStatus.ACTIVE,
                    null,
                    tokens.accessToken(),
                    tokens.refreshToken(),
                    tokens.expiresAt(),
                    null,
                    null,
                    null,
                    null,
                    Instant.now(),
                    Instant.now()
            ));

            Instant now = Instant.now();
            AppUserEntity userEntity = appUserRepository.findByAuthProviderAndExternalAccountId(AuthProvider.ATLASSIAN, accountSummary.accountId())
                    .map(existing -> updateExistingUser(existing, accountSummary, now))
                    .orElseGet(() -> createNewUser(accountSummary, now));
            AppUser persistedUser = appUserMapper.toDomain(appUserRepository.save(userEntity));

            String rawSessionToken = generateSessionToken();
            Instant expiresAt = now.plus(authProperties.sessionTtl());
            AppSessionEntity sessionEntity = new AppSessionEntity();
            AppUserEntity userReference = new AppUserEntity();
            userReference.setId(persistedUser.id());
            sessionEntity.setUser(userReference);
            sessionEntity.setSessionTokenHash(sessionTokenHasher.hash(rawSessionToken));
            sessionEntity.setExpiresAt(expiresAt);
            appSessionRepository.save(sessionEntity);

            AuthenticatedSession session = appSessionMapper.toDomain(persistedUser.id(), rawSessionToken, expiresAt);
            markConsumed(loginState);
            AppUserEntity principalSource = new AppUserEntity();
            principalSource.setId(persistedUser.id());
            principalSource.setExternalAccountId(persistedUser.externalAccountId());
            principalSource.setEmail(persistedUser.email());
            principalSource.setDisplayName(persistedUser.displayName());
            principalSource.setAvatarUrl(persistedUser.avatarUrl());
            principalSource.setAuthProvider(persistedUser.authProvider());
            principalSource.setLastLoginAt(persistedUser.lastLoginAt());
            return new LoginResult(
                    appUserMapper.toAuthenticatedUser(principalSource, expiresAt),
                    session,
                    loginState.postLoginRedirectUri()
            );
        } catch (RuntimeException exception) {
            markConsumed(loginState);
            throw exception;
        }
    }

    private AuthLoginState getActiveState(String state) {
        return authLoginStateRepository.findByStateAndConsumedFalseAndExpiresAtAfter(state, Instant.now())
                .map(authLoginStateMapper::toDomain)
                .orElseThrow(() -> new InvalidOAuthStateException(state));
    }

    private AppUserEntity updateExistingUser(AppUserEntity existing, JiraAccountSummary accountSummary, Instant now) {
        existing.setEmail(accountSummary.emailAddress());
        existing.setDisplayName(StringUtils.hasText(accountSummary.displayName()) ? accountSummary.displayName() : existing.getDisplayName());
        existing.setAvatarUrl(accountSummary.avatarUrl());
        existing.setLastLoginAt(now);
        return existing;
    }

    private AppUserEntity createNewUser(JiraAccountSummary accountSummary, Instant now) {
        AppUserEntity entity = new AppUserEntity();
        entity.setExternalAccountId(accountSummary.accountId());
        entity.setEmail(accountSummary.emailAddress());
        entity.setDisplayName(StringUtils.hasText(accountSummary.displayName()) ? accountSummary.displayName() : accountSummary.accountId());
        entity.setAvatarUrl(accountSummary.avatarUrl());
        entity.setAuthProvider(AuthProvider.ATLASSIAN);
        entity.setLastLoginAt(now);
        return entity;
    }

    private void markConsumed(AuthLoginState loginState) {
        authLoginStateRepository.save(authLoginStateMapper.toEntity(new AuthLoginState(
                loginState.id(),
                loginState.state(),
                loginState.postLoginRedirectUri(),
                loginState.expiresAt(),
                true,
                loginState.createdAt(),
                Instant.now()
        )));
    }

    private String generateSessionToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalizeUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
