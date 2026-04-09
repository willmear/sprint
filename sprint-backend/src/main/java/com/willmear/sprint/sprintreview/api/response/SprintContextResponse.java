package com.willmear.sprint.sprintreview.api.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SprintContextResponse(
        UUID workspaceId,
        UUID jiraConnectionId,
        Long externalSprintId,
        String sprintName,
        String sprintGoal,
        String sprintState,
        Instant sprintStartDate,
        Instant sprintEndDate,
        List<IssueSummaryResponse> completedIssues,
        List<IssueSummaryResponse> inProgressIssues,
        List<IssueSummaryResponse> carriedOverIssues,
        List<IssueSummaryResponse> bugFixes,
        List<IssueSummaryResponse> technicalImprovements,
        List<IssueSummaryResponse> allIssues,
        List<String> notableComments,
        List<String> blockers,
        Integer totalIssueCount,
        Integer totalCommentCount,
        Integer totalChangelogCount,
        Instant assembledAt
) {
}
