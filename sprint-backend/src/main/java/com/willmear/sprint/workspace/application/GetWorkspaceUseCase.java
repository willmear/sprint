package com.willmear.sprint.workspace.application;

import com.willmear.sprint.workspace.domain.model.Workspace;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetWorkspaceUseCase {

    private final WorkspaceAuthorizationService workspaceAuthorizationService;

    public GetWorkspaceUseCase(WorkspaceAuthorizationService workspaceAuthorizationService) {
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    public Workspace get(UUID workspaceId) {
        return workspaceAuthorizationService.requireOwnedWorkspace(workspaceId);
    }
}
