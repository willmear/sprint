package com.willmear.sprint.api.response;

import java.time.Instant;

public record IssueResponse(
        String issueKey,
        String externalIssueId,
        String summary,
        String description,
        String issueType,
        String status,
        String priority,
        String assigneeDisplayName,
        String reporterDisplayName,
        Integer storyPoints,
        Instant createdAtExternal,
        Instant updatedAtExternal
) {
}
