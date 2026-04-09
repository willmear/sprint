package com.willmear.sprint.common.exception;

import java.util.UUID;

public class JiraConnectionUnauthorizedException extends BadRequestException {

    public JiraConnectionUnauthorizedException(UUID workspaceId, UUID connectionId) {
        super("Jira connection is not authorized for workspace " + workspaceId + ": " + connectionId);
    }
}
