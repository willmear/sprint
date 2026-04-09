package com.willmear.sprint.common.exception;

import java.util.UUID;

public class WorkspaceNotFoundException extends NotFoundException {

    public WorkspaceNotFoundException(UUID workspaceId) {
        super("Workspace not found: " + workspaceId);
    }
}
