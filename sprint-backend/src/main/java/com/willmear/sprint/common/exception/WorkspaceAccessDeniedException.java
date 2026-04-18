package com.willmear.sprint.common.exception;

import java.util.UUID;

public class WorkspaceAccessDeniedException extends RuntimeException {

    public WorkspaceAccessDeniedException(UUID workspaceId) {
        super("Access denied for workspace: " + workspaceId);
    }
}
