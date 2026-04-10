package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.InvalidOAuthStateException;
import com.willmear.sprint.common.exception.JiraConnectionNotFoundException;
import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompleteJiraOAuthConnectionUseCase {

    private final JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final JiraOAuthClientPort jiraOAuthClientPort;
    private final JiraClientPort jiraClientPort;

    public CompleteJiraOAuthConnectionUseCase(
            JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            JiraOAuthClientPort jiraOAuthClientPort,
            JiraClientPort jiraClientPort
    ) {
        this.jiraOAuthStateRepositoryPort = jiraOAuthStateRepositoryPort;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.jiraOAuthClientPort = jiraOAuthClientPort;
        this.jiraClientPort = jiraClientPort;
    }

    public JiraConnection complete(String code, String state) {
        JiraOAuthState jiraOAuthState = jiraOAuthStateRepositoryPort.findActiveByState(state, Instant.now())
                .orElseThrow(() -> new InvalidOAuthStateException(state));

        JiraConnection existingConnection = jiraConnectionRepositoryPort
                .findByIdAndWorkspaceId(jiraOAuthState.connectionId(), jiraOAuthState.workspaceId())
                .orElseThrow(() -> new JiraConnectionNotFoundException(jiraOAuthState.workspaceId(), jiraOAuthState.connectionId()));

        JiraOAuthTokenResponse tokens = null;
        String resolvedBaseUrl = existingConnection.baseUrl();
        try {
            tokens = jiraOAuthClientPort.exchangeCodeForTokens(code, jiraOAuthState.redirectUri());
            JiraAccessibleResource resource = resolveAccessibleResource(existingConnection.baseUrl(), tokens.accessToken());
            resolvedBaseUrl = resource.url();
            JiraConnection authorizedConnection = jiraConnectionRepositoryPort.save(new JiraConnection(
                    existingConnection.id(),
                    existingConnection.workspaceId(),
                    resolvedBaseUrl,
                    existingConnection.authType(),
                    JiraConnectionStatus.AUTHORIZED,
                    existingConnection.clientEmailOrUsername(),
                    tokens.accessToken(),
                    tokens.refreshToken(),
                    tokens.expiresAt(),
                    existingConnection.lastTestedAt(),
                    existingConnection.externalAccountId(),
                    existingConnection.externalAccountDisplayName(),
                    existingConnection.externalAccountAvatarUrl(),
                    existingConnection.createdAt(),
                    Instant.now()
            ));

            JiraAccountSummary accountSummary = jiraClientPort.getCurrentAccount(authorizedConnection);
            JiraConnection activeConnection = jiraConnectionRepositoryPort.save(new JiraConnection(
                    authorizedConnection.id(),
                    authorizedConnection.workspaceId(),
                    authorizedConnection.baseUrl(),
                    authorizedConnection.authType(),
                    JiraConnectionStatus.ACTIVE,
                    accountSummary.emailAddress(),
                    authorizedConnection.encryptedAccessToken(),
                    authorizedConnection.encryptedRefreshToken(),
                    authorizedConnection.tokenExpiresAt(),
                    Instant.now(),
                    accountSummary.accountId(),
                    accountSummary.displayName(),
                    accountSummary.avatarUrl(),
                    authorizedConnection.createdAt(),
                    Instant.now()
            ));

            markStateConsumed(jiraOAuthState);
            return activeConnection;
        } catch (RuntimeException exception) {
            jiraConnectionRepositoryPort.save(new JiraConnection(
                    existingConnection.id(),
                    existingConnection.workspaceId(),
                    resolvedBaseUrl,
                    existingConnection.authType(),
                    JiraConnectionStatus.FAILED,
                    existingConnection.clientEmailOrUsername(),
                    tokens != null ? tokens.accessToken() : existingConnection.encryptedAccessToken(),
                    tokens != null ? tokens.refreshToken() : existingConnection.encryptedRefreshToken(),
                    tokens != null ? tokens.expiresAt() : existingConnection.tokenExpiresAt(),
                    existingConnection.lastTestedAt(),
                    existingConnection.externalAccountId(),
                    existingConnection.externalAccountDisplayName(),
                    existingConnection.externalAccountAvatarUrl(),
                    existingConnection.createdAt(),
                    Instant.now()
            ));
            markStateConsumed(jiraOAuthState);
            throw exception;
        }
    }

    private JiraAccessibleResource resolveAccessibleResource(String requestedBaseUrl, String accessToken) {
        List<JiraAccessibleResource> resources = jiraOAuthClientPort.getAccessibleResources(accessToken);
        if (resources.isEmpty()) {
            throw new JiraOAuthException("No accessible Jira sites were returned for the authorized Atlassian account.");
        }

        String normalizedRequestedBaseUrl = normalizeUrl(requestedBaseUrl);
        if (StringUtils.hasText(normalizedRequestedBaseUrl) && !normalizedRequestedBaseUrl.contains("pending-jira-site.invalid")) {
            return resources.stream()
                    .filter(resource -> Objects.equals(normalizeUrl(resource.url()), normalizedRequestedBaseUrl))
                    .findFirst()
                    .orElseThrow(() -> new JiraOAuthException("The requested Jira site is not accessible for the authorized Atlassian account."));
        }

        return resources.stream()
                .min(Comparator.comparing(resource -> normalizeUrl(resource.url()) == null ? "" : normalizeUrl(resource.url())))
                .orElseThrow(() -> new JiraOAuthException("No accessible Jira sites were returned for the authorized Atlassian account."));
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

    private void markStateConsumed(JiraOAuthState jiraOAuthState) {
        jiraOAuthStateRepositoryPort.save(new JiraOAuthState(
                jiraOAuthState.id(),
                jiraOAuthState.workspaceId(),
                jiraOAuthState.connectionId(),
                jiraOAuthState.state(),
                jiraOAuthState.redirectUri(),
                jiraOAuthState.expiresAt(),
                true,
                jiraOAuthState.createdAt(),
                Instant.now()
        ));
    }
}
