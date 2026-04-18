package com.willmear.sprint.common.exception;

import java.util.UUID;

public class ResourceAccessDeniedException extends RuntimeException {

    public ResourceAccessDeniedException(String resourceType, UUID resourceId) {
        super("Access denied for " + resourceType + ": " + resourceId);
    }

    public ResourceAccessDeniedException(String resourceType, UUID resourceId, Throwable cause) {
        super("Access denied for " + resourceType + ": " + resourceId, cause);
    }
}
