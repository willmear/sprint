package com.willmear.sprint.workspace.application;

import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListWorkspacesUseCase {

    private final CurrentUserService currentUserService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public ListWorkspacesUseCase(
            CurrentUserService currentUserService,
            WorkspaceRepository workspaceRepository,
            WorkspaceMapper workspaceMapper
    ) {
        this.currentUserService = currentUserService;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public List<Workspace> list() {
        return workspaceRepository.findByOwner_IdOrderByCreatedAtAsc(currentUserService.requireCurrentUserId()).stream()
                .map(workspaceMapper::toDomain)
                .toList();
    }
}
