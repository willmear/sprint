package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.domain.AuthLoginState;
import com.willmear.sprint.auth.mapper.AuthLoginStateMapper;
import com.willmear.sprint.auth.repository.AuthLoginStateRepository;
import com.willmear.sprint.auth.security.AuthProperties;
import com.willmear.sprint.config.UiProperties;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartLoginWithJiraUseCase {

    private final AuthLoginStateRepository authLoginStateRepository;
    private final AuthLoginStateMapper authLoginStateMapper;
    private final JiraOAuthClientPort jiraOAuthClientPort;
    private final AuthProperties authProperties;
    private final UiProperties uiProperties;

    public StartLoginWithJiraUseCase(
            AuthLoginStateRepository authLoginStateRepository,
            AuthLoginStateMapper authLoginStateMapper,
            JiraOAuthClientPort jiraOAuthClientPort,
            AuthProperties authProperties,
            UiProperties uiProperties
    ) {
        this.authLoginStateRepository = authLoginStateRepository;
        this.authLoginStateMapper = authLoginStateMapper;
        this.jiraOAuthClientPort = jiraOAuthClientPort;
        this.authProperties = authProperties;
        this.uiProperties = uiProperties;
    }

    @Transactional
    public String start(String redirectTo) {
        Instant now = Instant.now();
        String state = UUID.randomUUID().toString();
        String postLoginRedirectUri = resolvePostLoginRedirectUri(redirectTo);
        authLoginStateRepository.save(authLoginStateMapper.toEntity(new AuthLoginState(
                null,
                state,
                postLoginRedirectUri,
                now.plus(authProperties.loginStateTtl()),
                false,
                now,
                now
        )));
        return jiraOAuthClientPort.buildAuthorizationUrl(null, null, state, authProperties.jiraLoginRedirectUri());
    }

    @Transactional(readOnly = true)
    public Optional<String> findActiveRedirect(String state) {
        return authLoginStateRepository.findByStateAndConsumedFalseAndExpiresAtAfter(state, Instant.now())
                .map(AuthLoginStateEntity -> AuthLoginStateEntity.getPostLoginRedirectUri());
    }

    private String resolvePostLoginRedirectUri(String redirectTo) {
        String defaultRedirectUri = uiProperties.allowedOrigin() + authProperties.defaultPostLoginPath();
        if (redirectTo == null || redirectTo.isBlank()) {
            return defaultRedirectUri;
        }
        if (redirectTo.startsWith("/")) {
            return uiProperties.allowedOrigin() + redirectTo;
        }
        URI allowedOrigin = URI.create(uiProperties.allowedOrigin());
        URI requested = URI.create(redirectTo);
        if (allowedOrigin.getScheme().equalsIgnoreCase(requested.getScheme())
                && allowedOrigin.getHost().equalsIgnoreCase(requested.getHost())
                && allowedOrigin.getPort() == requested.getPort()) {
            return requested.toString();
        }
        return defaultRedirectUri;
    }
}
