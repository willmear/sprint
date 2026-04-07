package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.InvalidOAuthStateException;
import com.willmear.sprint.common.exception.JiraConnectionNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import org.springframework.stereotype.Service;

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

        JiraOAuthTokenResponse tokens = jiraOAuthClientPort.exchangeCodeForTokens(code, jiraOAuthState.redirectUri());
        JiraConnection authorizedConnection = jiraConnectionRepositoryPort.save(new JiraConnection(
                existingConnection.id(),
                existingConnection.workspaceId(),
                existingConnection.baseUrl(),
                existingConnection.authType(),
                JiraConnectionStatus.AUTHORIZED,
                existingConnection.clientEmailOrUsername(),
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresAt(),
                existingConnection.lastTestedAt(),
                existingConnection.externalAccountId(),
                existingConnection.externalAccountDisplayName(),
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
                authorizedConnection.createdAt(),
                Instant.now()
        ));

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

        return activeConnection;
    }
}
