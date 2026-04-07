package com.willmear.sprint.sprintreview.domain.model;

public record IssueSummary(
        String issueKey,
        String summary,
        String description,
        String issueType,
        String status,
        String priority,
        String assigneeDisplayName,
        Integer storyPoints,
        boolean bugFix,
        boolean technicalWork
) {
}
