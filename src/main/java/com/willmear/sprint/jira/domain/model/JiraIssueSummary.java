package com.willmear.sprint.jira.domain.model;

public record JiraIssueSummary(
        String issueKey,
        String summary,
        String status,
        Integer storyPoints
) {
}
