package com.willmear.sprint.common.exception;

import java.util.UUID;

public class SprintNotFoundException extends NotFoundException {

    public SprintNotFoundException(UUID workspaceId, Long externalSprintId) {
        super("Sprint not found for workspace " + workspaceId + ": " + externalSprintId);
    }
}
