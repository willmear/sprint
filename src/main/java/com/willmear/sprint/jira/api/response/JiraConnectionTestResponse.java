package com.willmear.sprint.jira.api.response;

public record JiraConnectionTestResponse(
        boolean success,
        String message,
        String accountId,
        String displayName,
        String emailAddress
) {
}
