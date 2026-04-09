package com.willmear.sprint.workspace.application;

import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public CreateWorkspaceUseCase(WorkspaceRepository workspaceRepository, WorkspaceMapper workspaceMapper) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public Workspace create(String name, String description) {
        WorkspaceEntity entity = new WorkspaceEntity();
        entity.setName(name);
        entity.setDescription(description);
        WorkspaceEntity saved = workspaceRepository.save(entity);
        return workspaceMapper.toDomain(saved);
    }
}
