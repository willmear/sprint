package com.willmear.sprint.common.exception;

import java.util.UUID;

public class JiraConnectionNotFoundException extends NotFoundException {

    public JiraConnectionNotFoundException(UUID workspaceId, UUID connectionId) {
        super("Jira connection not found for workspace " + workspaceId + ": " + connectionId);
    }
}
