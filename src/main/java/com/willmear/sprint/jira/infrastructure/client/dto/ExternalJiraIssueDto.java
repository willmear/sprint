package com.willmear.sprint.jira.infrastructure.client.dto;

public record ExternalJiraIssueDto(
        String id,
        String key,
        ExternalJiraIssueFieldsDto fields
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}
