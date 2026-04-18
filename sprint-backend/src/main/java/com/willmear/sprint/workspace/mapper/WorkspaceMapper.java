package com.willmear.sprint.workspace.mapper;

import com.willmear.sprint.api.response.WorkspaceResponse;
import com.willmear.sprint.api.response.WorkspaceSummaryResponse;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {

    public Workspace toDomain(WorkspaceEntity entity) {
        return new Workspace(
                entity.getId(),
                entity.getOwner() != null ? entity.getOwner().getId() : null,
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public WorkspaceResponse toResponse(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.id(),
                workspace.name(),
                workspace.description(),
                workspace.createdAt(),
                workspace.updatedAt()
        );
    }

    public WorkspaceSummaryResponse toSummaryResponse(Workspace workspace) {
        return new WorkspaceSummaryResponse(
                workspace.id(),
                workspace.name(),
                workspace.description(),
                workspace.createdAt(),
                workspace.updatedAt()
        );
    }
}
