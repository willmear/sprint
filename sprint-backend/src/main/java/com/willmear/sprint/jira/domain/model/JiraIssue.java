package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraIssue(
        UUID workspaceId,
        UUID jiraConnectionId,
        Long externalSprintId,
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
