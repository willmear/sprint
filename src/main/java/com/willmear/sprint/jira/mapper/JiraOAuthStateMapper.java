package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraOAuthStateEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraOAuthStateMapper {

    public JiraOAuthState toDomain(JiraOAuthStateEntity entity) {
        UUID workspaceId = entity.getWorkspace() != null ? entity.getWorkspace().getId() : null;
        UUID connectionId = entity.getConnection() != null ? entity.getConnection().getId() : null;
        return new JiraOAuthState(
                entity.getId(),
                workspaceId,
                connectionId,
                entity.getState(),
                entity.getRedirectUri(),
                entity.getExpiresAt(),
                entity.isConsumed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public JiraOAuthStateEntity toEntity(JiraOAuthState jiraOAuthState) {
        JiraOAuthStateEntity entity = new JiraOAuthStateEntity();
        entity.setId(jiraOAuthState.id());
        entity.setWorkspace(toWorkspaceReference(jiraOAuthState.workspaceId()));
        entity.setConnection(toConnectionReference(jiraOAuthState.connectionId()));
        entity.setState(jiraOAuthState.state());
        entity.setRedirectUri(jiraOAuthState.redirectUri());
        entity.setExpiresAt(jiraOAuthState.expiresAt());
        entity.setConsumed(jiraOAuthState.consumed());
        entity.setCreatedAt(jiraOAuthState.createdAt());
        entity.setUpdatedAt(jiraOAuthState.updatedAt());
        return entity;
    }

    private WorkspaceEntity toWorkspaceReference(UUID workspaceId) {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        return workspace;
    }

    private JiraConnectionEntity toConnectionReference(UUID connectionId) {
        if (connectionId == null) {
            return null;
        }
        JiraConnectionEntity connection = new JiraConnectionEntity();
        connection.setId(connectionId);
        return connection;
    }
}
