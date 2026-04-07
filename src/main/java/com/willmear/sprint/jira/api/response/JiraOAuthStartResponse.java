package com.willmear.sprint.jira.api.response;

import java.util.UUID;

public record JiraOAuthStartResponse(
        UUID connectionId,
        String state,
        String authorizationUrl
) {
}
