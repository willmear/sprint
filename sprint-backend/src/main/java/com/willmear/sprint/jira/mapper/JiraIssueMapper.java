package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.api.response.IssueResponse;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraIssueMapper {

    public JiraIssue toDomain(JiraIssueEntity entity) {
        return new JiraIssue(
                entity.getWorkspace() != null ? entity.getWorkspace().getId() : null,
                entity.getJiraConnection() != null ? entity.getJiraConnection().getId() : null,
                entity.getExternalSprintId(),
                entity.getIssueKey(),
                entity.getExternalIssueId(),
                entity.getSummary(),
                entity.getDescription(),
                entity.getIssueType(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAssigneeDisplayName(),
                entity.getReporterDisplayName(),
                entity.getStoryPoints(),
                entity.getCreatedAtExternal(),
                entity.getUpdatedAtExternal()
        );
    }

    public JiraIssueEntity toEntity(UUID workspaceId, UUID connectionId, UUID sprintEntityId, Long externalSprintId, JiraIssue issue) {
        JiraIssueEntity entity = new JiraIssueEntity();
        entity.setWorkspace(toWorkspaceReference(workspaceId));
        entity.setJiraConnection(toConnectionReference(connectionId));
        entity.setJiraSprint(toSprintReference(sprintEntityId));
        entity.setExternalSprintId(externalSprintId);
        entity.setIssueKey(issue.issueKey());
        entity.setExternalIssueId(issue.externalIssueId());
        entity.setSummary(issue.summary());
        entity.setDescription(issue.description());
        entity.setIssueType(issue.issueType());
        entity.setStatus(issue.status());
        entity.setPriority(issue.priority());
        entity.setAssigneeDisplayName(issue.assigneeDisplayName());
        entity.setReporterDisplayName(issue.reporterDisplayName());
        entity.setStoryPoints(issue.storyPoints());
        entity.setCreatedAtExternal(issue.createdAtExternal());
        entity.setUpdatedAtExternal(issue.updatedAtExternal());
        return entity;
    }

    public IssueResponse toResponse(JiraIssue issue) {
        return new IssueResponse(
                issue.issueKey(),
                issue.externalIssueId(),
                issue.summary(),
                issue.description(),
                issue.issueType(),
                issue.status(),
                issue.priority(),
                issue.assigneeDisplayName(),
                issue.reporterDisplayName(),
                issue.storyPoints(),
                issue.createdAtExternal(),
                issue.updatedAtExternal()
        );
    }

    private WorkspaceEntity toWorkspaceReference(UUID workspaceId) {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        return workspace;
    }

    private JiraConnectionEntity toConnectionReference(UUID connectionId) {
        JiraConnectionEntity jiraConnection = new JiraConnectionEntity();
        jiraConnection.setId(connectionId);
        return jiraConnection;
    }

    private JiraSprintEntity toSprintReference(UUID sprintId) {
        JiraSprintEntity sprint = new JiraSprintEntity();
        sprint.setId(sprintId);
        return sprint;
    }
}
