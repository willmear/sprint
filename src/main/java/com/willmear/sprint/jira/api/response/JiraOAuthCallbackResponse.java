package com.willmear.sprint.jira.api.response;

import java.util.UUID;

public record JiraOAuthCallbackResponse(
        UUID connectionId,
        String status,
        String externalAccountDisplayName,
        String message
) {
}
