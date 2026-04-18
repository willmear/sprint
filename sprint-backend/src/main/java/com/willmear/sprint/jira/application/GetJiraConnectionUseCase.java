package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.JiraConnectionNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.workspace.application.WorkspaceAuthorizationService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetJiraConnectionUseCase {

    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;

    public GetJiraConnectionUseCase(
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            WorkspaceAuthorizationService workspaceAuthorizationService
    ) {
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    public JiraConnection get(UUID workspaceId, UUID connectionId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return jiraConnectionRepositoryPort.findByIdAndWorkspaceId(connectionId, workspaceId)
                .orElseThrow(() -> new JiraConnectionNotFoundException(workspaceId, connectionId));
    }
}
