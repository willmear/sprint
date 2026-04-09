package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.api.JiraConnectionService.StartOAuthConnectionResult;
import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StartJiraOAuthConnectionUseCase {

    private static final Duration OAUTH_STATE_TTL = Duration.ofMinutes(15);
    private static final String PENDING_JIRA_BASE_URL = "https://pending-jira-site.invalid";

    private final WorkspaceService workspaceService;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    private final JiraOAuthClientPort jiraOAuthClientPort;

    public StartJiraOAuthConnectionUseCase(
            WorkspaceService workspaceService,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort,
            JiraOAuthClientPort jiraOAuthClientPort
    ) {
        this.workspaceService = workspaceService;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.jiraOAuthStateRepositoryPort = jiraOAuthStateRepositoryPort;
        this.jiraOAuthClientPort = jiraOAuthClientPort;
    }

    public StartOAuthConnectionResult start(UUID workspaceId, StartJiraOAuthConnectionRequest request) {
        workspaceService.getWorkspace(workspaceId);

        Instant now = Instant.now();
        JiraConnection pendingConnection = jiraConnectionRepositoryPort.save(new JiraConnection(
                null,
                workspaceId,
                request.baseUrl() != null && !request.baseUrl().isBlank() ? request.baseUrl() : PENDING_JIRA_BASE_URL,
                JiraAuthType.OAUTH,
                JiraConnectionStatus.PENDING_AUTHORIZATION,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now
        ));

        String state = UUID.randomUUID().toString();
        JiraOAuthState oauthState = new JiraOAuthState(
                null,
                workspaceId,
                pendingConnection.id(),
                state,
                request.redirectUri(),
                now.plus(OAUTH_STATE_TTL),
                false,
                now,
                now
        );
        jiraOAuthStateRepositoryPort.save(oauthState);

        String authorizationUrl = jiraOAuthClientPort.buildAuthorizationUrl(
                workspaceId,
                pendingConnection.id(),
                state,
                request.redirectUri()
        );

        return new StartOAuthConnectionResult(pendingConnection.id(), state, authorizationUrl);
    }
}
