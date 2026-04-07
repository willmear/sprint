package com.willmear.sprint.sprintreview.api.response;

public record IssueSummaryResponse(
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
