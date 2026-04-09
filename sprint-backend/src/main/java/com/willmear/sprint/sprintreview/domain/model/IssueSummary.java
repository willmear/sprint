package com.willmear.sprint.sprintreview.domain.model;

import java.util.List;

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
        boolean technicalWork,
        List<IssueCommentSummary> comments
) {
}
