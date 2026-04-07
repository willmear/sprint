package com.willmear.sprint.workspace.service;

import com.willmear.sprint.workspace.api.WorkspaceService;
import com.willmear.sprint.workspace.application.CreateWorkspaceUseCase;
import com.willmear.sprint.workspace.application.GetWorkspaceUseCase;
import com.willmear.sprint.workspace.application.ListWorkspacesUseCase;
import com.willmear.sprint.workspace.domain.model.Workspace;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceApplicationService implements WorkspaceService {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final GetWorkspaceUseCase getWorkspaceUseCase;
    private final ListWorkspacesUseCase listWorkspacesUseCase;

    public WorkspaceApplicationService(
            CreateWorkspaceUseCase createWorkspaceUseCase,
            GetWorkspaceUseCase getWorkspaceUseCase,
            ListWorkspacesUseCase listWorkspacesUseCase
    ) {
        this.createWorkspaceUseCase = createWorkspaceUseCase;
        this.getWorkspaceUseCase = getWorkspaceUseCase;
        this.listWorkspacesUseCase = listWorkspacesUseCase;
    }

    @Override
    public Workspace createWorkspace(String name, String description) {
        return createWorkspaceUseCase.create(name, description);
    }

    @Override
    public Workspace getWorkspace(UUID workspaceId) {
        return getWorkspaceUseCase.get(workspaceId);
    }

    @Override
    public List<Workspace> listWorkspaces() {
        return listWorkspacesUseCase.list();
    }
}
