package com.willmear.sprint.common.exception;

import java.util.UUID;

public class SprintReviewNotAvailableException extends NotFoundException {

    public SprintReviewNotAvailableException(UUID workspaceId, Long externalSprintId) {
        super("Sprint review is not yet persisted for workspace " + workspaceId + " and sprint " + externalSprintId + ".");
    }
}
