package com.willmear.sprint.jira.infrastructure.client.dto;

public record ExternalJiraUserDto(
        String accountId,
        String displayName,
        String emailAddress
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}
