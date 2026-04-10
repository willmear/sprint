package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.jira.api.response.JiraConnectionResponse;
import com.willmear.sprint.jira.api.response.JiraConnectionSummaryResponse;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraConnectionMapper {

    public JiraConnection toDomain(JiraConnectionEntity entity) {
        UUID workspaceId = entity.getWorkspace() != null ? entity.getWorkspace().getId() : null;
        return new JiraConnection(
                entity.getId(),
                workspaceId,
                entity.getBaseUrl(),
                entity.getAuthType(),
                entity.getStatus(),
                entity.getClientEmailOrUsername(),
                entity.getEncryptedAccessToken(),
                entity.getEncryptedRefreshToken(),
                entity.getTokenExpiresAt(),
                entity.getLastTestedAt(),
                entity.getExternalAccountId(),
                entity.getExternalAccountDisplayName(),
                entity.getExternalAccountAvatarUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public JiraConnectionEntity toEntity(JiraConnection connection) {
        JiraConnectionEntity entity = new JiraConnectionEntity();
        entity.setId(connection.id());
        entity.setWorkspace(toWorkspaceReference(connection.workspaceId()));
        entity.setBaseUrl(connection.baseUrl());
        entity.setAuthType(connection.authType());
        entity.setStatus(connection.status() != null ? connection.status() : JiraConnectionStatus.PENDING_AUTHORIZATION);
        entity.setClientEmailOrUsername(connection.clientEmailOrUsername());
        entity.setEncryptedAccessToken(connection.encryptedAccessToken());
        entity.setEncryptedRefreshToken(connection.encryptedRefreshToken());
        entity.setTokenExpiresAt(connection.tokenExpiresAt());
        entity.setLastTestedAt(connection.lastTestedAt());
        entity.setExternalAccountId(connection.externalAccountId());
        entity.setExternalAccountDisplayName(connection.externalAccountDisplayName());
        entity.setExternalAccountAvatarUrl(connection.externalAccountAvatarUrl());
        entity.setCreatedAt(connection.createdAt() != null ? connection.createdAt() : Instant.now());
        entity.setUpdatedAt(connection.updatedAt() != null ? connection.updatedAt() : Instant.now());
        return entity;
    }

    public JiraConnectionResponse toResponse(JiraConnection connection) {
        return new JiraConnectionResponse(
                connection.id(),
                connection.workspaceId(),
                connection.baseUrl(),
                connection.authType().name(),
                connection.status().name(),
                connection.clientEmailOrUsername(),
                connection.externalAccountId(),
                connection.externalAccountDisplayName(),
                connection.externalAccountAvatarUrl(),
                connection.tokenExpiresAt(),
                connection.lastTestedAt(),
                connection.createdAt(),
                connection.updatedAt()
        );
    }

    public JiraConnectionSummaryResponse toSummaryResponse(JiraConnection connection) {
        return new JiraConnectionSummaryResponse(
                connection.id(),
                connection.workspaceId(),
                connection.baseUrl(),
                connection.authType().name(),
                connection.status().name(),
                connection.externalAccountDisplayName(),
                connection.externalAccountAvatarUrl(),
                connection.lastTestedAt(),
                connection.createdAt()
        );
    }

    private WorkspaceEntity toWorkspaceReference(UUID workspaceId) {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        return workspace;
    }
}
