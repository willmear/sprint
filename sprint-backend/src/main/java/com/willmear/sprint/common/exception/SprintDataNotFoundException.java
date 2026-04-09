package com.willmear.sprint.common.exception;

import java.util.UUID;

public class SprintDataNotFoundException extends NotFoundException {

    public SprintDataNotFoundException(UUID workspaceId, Long externalSprintId) {
        super("Synced sprint data not found for workspace " + workspaceId + " and sprint " + externalSprintId);
    }
}
