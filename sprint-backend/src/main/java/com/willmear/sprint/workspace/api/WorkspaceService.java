package com.willmear.sprint.workspace.api;

import com.willmear.sprint.workspace.domain.model.Workspace;
import java.util.List;
import java.util.UUID;

public interface WorkspaceService {

    Workspace createWorkspace(String name, String description);

    Workspace getWorkspace(UUID workspaceId);

    List<Workspace> listWorkspaces();
}
