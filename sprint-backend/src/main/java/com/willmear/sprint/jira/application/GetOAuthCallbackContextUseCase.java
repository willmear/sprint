package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.api.JiraConnectionService.OAuthCallbackContext;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GetOAuthCallbackContextUseCase {

    private final JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;

    public GetOAuthCallbackContextUseCase(JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort) {
        this.jiraOAuthStateRepositoryPort = jiraOAuthStateRepositoryPort;
    }

    public Optional<OAuthCallbackContext> find(String state) {
        return jiraOAuthStateRepositoryPort.findActiveByState(state, Instant.now())
                .map(jiraOAuthState -> new OAuthCallbackContext(jiraOAuthState.workspaceId(), jiraOAuthState.connectionId()));
    }
}
