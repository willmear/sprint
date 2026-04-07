package com.willmear.sprint.jira.infrastructure.client.dto;

import java.time.Instant;

public record ExternalJiraIssueFieldsDto(
        String summary,
        String description,
        String issueType,
        String status,
        String priority,
        ExternalJiraUserDto assignee,
        ExternalJiraUserDto reporter,
        Integer storyPoints,
        Instant created,
        Instant updated
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}
