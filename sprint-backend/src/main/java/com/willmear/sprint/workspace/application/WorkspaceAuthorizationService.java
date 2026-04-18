package com.willmear.sprint.workspace.application;

import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.common.exception.WorkspaceAccessDeniedException;
import com.willmear.sprint.common.exception.WorkspaceNotFoundException;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceAuthorizationService {

    private final CurrentUserService currentUserService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public WorkspaceAuthorizationService(
            CurrentUserService currentUserService,
            WorkspaceRepository workspaceRepository,
            WorkspaceMapper workspaceMapper
    ) {
        this.currentUserService = currentUserService;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public Workspace requireOwnedWorkspace(UUID workspaceId) {
        UUID currentUserId = currentUserService.requireCurrentUserId();
        return workspaceRepository.findByIdAndOwner_Id(workspaceId, currentUserId)
                .map(workspaceMapper::toDomain)
                .orElseGet(() -> {
                    if (workspaceRepository.existsById(workspaceId)) {
                        throw new WorkspaceAccessDeniedException(workspaceId);
                    }
                    throw new WorkspaceNotFoundException(workspaceId);
                });
    }

    public void ensureCanAccessWorkspace(UUID workspaceId) {
        requireOwnedWorkspace(workspaceId);
    }
}
