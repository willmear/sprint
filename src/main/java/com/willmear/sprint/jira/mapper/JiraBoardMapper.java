package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.jira.domain.model.JiraBoard;
import com.willmear.sprint.jira.infrastructure.entity.JiraBoardEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraBoardMapper {

    public JiraBoard toDomain(JiraBoardEntity entity) {
        UUID workspaceId = entity.getWorkspace() != null ? entity.getWorkspace().getId() : null;
        return new JiraBoard(
                entity.getExternalBoardId(),
                workspaceId,
                entity.getName(),
                entity.getBoardType(),
                entity.getProjectKey()
        );
    }

    public JiraBoardEntity updateEntity(JiraBoardEntity entity, JiraBoard board) {
        entity.setWorkspace(toWorkspaceReference(board.workspaceId()));
        entity.setExternalBoardId(board.externalBoardId());
        entity.setName(board.name());
        entity.setBoardType(board.boardType());
        entity.setProjectKey(board.projectKey());
        return entity;
    }

    private WorkspaceEntity toWorkspaceReference(UUID workspaceId) {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        return workspace;
    }
}
