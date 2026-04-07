package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.port.JiraSprintRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListSprintsUseCase {

    private final WorkspaceService workspaceService;
    private final JiraSprintRepositoryPort jiraSprintRepositoryPort;

    public ListSprintsUseCase(WorkspaceService workspaceService, JiraSprintRepositoryPort jiraSprintRepositoryPort) {
        this.workspaceService = workspaceService;
        this.jiraSprintRepositoryPort = jiraSprintRepositoryPort;
    }

    public List<JiraSprint> list(UUID workspaceId) {
        workspaceService.getWorkspace(workspaceId);
        return jiraSprintRepositoryPort.findByWorkspaceId(workspaceId);
    }
}
