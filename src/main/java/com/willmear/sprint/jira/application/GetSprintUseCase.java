package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.SprintNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.port.JiraSprintRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetSprintUseCase {

    private final WorkspaceService workspaceService;
    private final JiraSprintRepositoryPort jiraSprintRepositoryPort;

    public GetSprintUseCase(WorkspaceService workspaceService, JiraSprintRepositoryPort jiraSprintRepositoryPort) {
        this.workspaceService = workspaceService;
        this.jiraSprintRepositoryPort = jiraSprintRepositoryPort;
    }

    public JiraSprint get(UUID workspaceId, Long sprintId) {
        workspaceService.getWorkspace(workspaceId);
        return jiraSprintRepositoryPort.findByWorkspaceIdAndExternalSprintId(workspaceId, sprintId)
                .orElseThrow(() -> new SprintNotFoundException(workspaceId, sprintId));
    }
}
