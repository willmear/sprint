package com.willmear.sprint.workspace.application;

import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateWorkspaceUseCase {

    private final CurrentUserService currentUserService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public CreateWorkspaceUseCase(
            CurrentUserService currentUserService,
            WorkspaceRepository workspaceRepository,
            WorkspaceMapper workspaceMapper
    ) {
        this.currentUserService = currentUserService;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public Workspace create(String name, String description) {
        AppUserEntity owner = new AppUserEntity();
        owner.setId(currentUserService.requireCurrentUserId());
        WorkspaceEntity entity = new WorkspaceEntity();
        entity.setOwner(owner);
        entity.setName(name);
        entity.setDescription(description);
        WorkspaceEntity saved = workspaceRepository.save(entity);
        return workspaceMapper.toDomain(saved);
    }
}
