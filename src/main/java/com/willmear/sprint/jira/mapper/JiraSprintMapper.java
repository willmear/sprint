package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.api.response.SprintResponse;
import com.willmear.sprint.api.response.SprintSummaryResponse;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraSprintMapper {

    public JiraSprint toDomain(JiraSprintEntity entity) {
        return new JiraSprint(
                entity.getExternalSprintId(),
                entity.getWorkspace() != null ? entity.getWorkspace().getId() : null,
                entity.getJiraConnection() != null ? entity.getJiraConnection().getId() : null,
                entity.getExternalBoardId(),
                entity.getName(),
                entity.getGoal(),
                entity.getState(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCompleteDate(),
                entity.getSyncedAt()
        );
    }

    public JiraSprintEntity updateEntity(JiraSprintEntity entity, JiraSprint sprint) {
        entity.setWorkspace(toWorkspaceReference(sprint.workspaceId()));
        entity.setJiraConnection(toConnectionReference(sprint.jiraConnectionId()));
        entity.setExternalSprintId(sprint.externalSprintId());
        entity.setExternalBoardId(sprint.externalBoardId());
        entity.setName(sprint.name());
        entity.setGoal(sprint.goal());
        entity.setState(sprint.state());
        entity.setStartDate(sprint.startDate());
        entity.setEndDate(sprint.endDate());
        entity.setCompleteDate(sprint.completeDate());
        entity.setSyncedAt(sprint.syncedAt());
        return entity;
    }

    public SprintSummaryResponse toSummaryResponse(JiraSprint sprint) {
        return new SprintSummaryResponse(
                sprint.externalSprintId(),
                sprint.name(),
                sprint.state(),
                sprint.startDate(),
                sprint.endDate(),
                sprint.completeDate(),
                sprint.syncedAt()
        );
    }

    public SprintResponse toResponse(JiraSprint sprint, int issueCount) {
        return new SprintResponse(
                sprint.externalSprintId(),
                sprint.name(),
                sprint.state(),
                sprint.goal(),
                sprint.externalBoardId(),
                issueCount,
                sprint.startDate(),
                sprint.endDate(),
                sprint.completeDate(),
                sprint.syncedAt()
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
}
