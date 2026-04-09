package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.SprintNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.port.JiraIssueRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraSprintRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetSprintIssuesUseCase {

    private final WorkspaceService workspaceService;
    private final JiraSprintRepositoryPort jiraSprintRepositoryPort;
    private final JiraIssueRepositoryPort jiraIssueRepositoryPort;

    public GetSprintIssuesUseCase(
            WorkspaceService workspaceService,
            JiraSprintRepositoryPort jiraSprintRepositoryPort,
            JiraIssueRepositoryPort jiraIssueRepositoryPort
    ) {
        this.workspaceService = workspaceService;
        this.jiraSprintRepositoryPort = jiraSprintRepositoryPort;
        this.jiraIssueRepositoryPort = jiraIssueRepositoryPort;
    }

    public List<JiraIssue> get(UUID workspaceId, Long sprintId) {
        workspaceService.getWorkspace(workspaceId);
        jiraSprintRepositoryPort.findByWorkspaceIdAndExternalSprintId(workspaceId, sprintId)
                .orElseThrow(() -> new SprintNotFoundException(workspaceId, sprintId));
        return jiraIssueRepositoryPort.findByWorkspaceIdAndExternalSprintId(workspaceId, sprintId);
    }
}
