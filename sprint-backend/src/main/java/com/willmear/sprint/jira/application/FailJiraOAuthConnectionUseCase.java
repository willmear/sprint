package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.InvalidOAuthStateException;
import com.willmear.sprint.common.exception.JiraConnectionNotFoundException;
import com.willmear.sprint.common.exception.JiraOAuthCallbackException;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class FailJiraOAuthConnectionUseCase {

    private final JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;

    public FailJiraOAuthConnectionUseCase(
            JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort
    ) {
        this.jiraOAuthStateRepositoryPort = jiraOAuthStateRepositoryPort;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
    }

    public void fail(String state, String error, String errorDescription) {
        JiraOAuthState jiraOAuthState = jiraOAuthStateRepositoryPort.findActiveByState(state, Instant.now())
                .orElseThrow(() -> new InvalidOAuthStateException(state));

        JiraConnection existingConnection = jiraConnectionRepositoryPort
                .findByIdAndWorkspaceId(jiraOAuthState.connectionId(), jiraOAuthState.workspaceId())
                .orElseThrow(() -> new JiraConnectionNotFoundException(jiraOAuthState.workspaceId(), jiraOAuthState.connectionId()));

        jiraConnectionRepositoryPort.save(new JiraConnection(
                existingConnection.id(),
                existingConnection.workspaceId(),
                existingConnection.baseUrl(),
                existingConnection.authType(),
                JiraConnectionStatus.FAILED,
                existingConnection.clientEmailOrUsername(),
                existingConnection.encryptedAccessToken(),
                existingConnection.encryptedRefreshToken(),
                existingConnection.tokenExpiresAt(),
                existingConnection.lastTestedAt(),
                existingConnection.externalAccountId(),
                existingConnection.externalAccountDisplayName(),
                existingConnection.externalAccountAvatarUrl(),
                existingConnection.createdAt(),
                Instant.now()
        ));

        markStateConsumed(jiraOAuthState);
        throw new JiraOAuthCallbackException(error, errorDescription);
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
