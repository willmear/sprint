package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListJiraConnectionsUseCase {

    private final WorkspaceService workspaceService;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;

    public ListJiraConnectionsUseCase(
            WorkspaceService workspaceService,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort
    ) {
        this.workspaceService = workspaceService;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
    }

    public List<JiraConnection> list(UUID workspaceId) {
        workspaceService.getWorkspace(workspaceId);
        return jiraConnectionRepositoryPort.findByWorkspaceId(workspaceId);
    }
}
