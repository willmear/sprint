package com.willmear.sprint.sprintreview.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SprintContext(
        UUID workspaceId,
        UUID jiraConnectionId,
        Long externalSprintId,
        String sprintName,
        String sprintGoal,
        String sprintState,
        Instant sprintStartDate,
        Instant sprintEndDate,
        List<IssueSummary> completedIssues,
        List<IssueSummary> inProgressIssues,
        List<IssueSummary> carriedOverIssues,
        List<IssueSummary> bugFixes,
        List<IssueSummary> technicalImprovements,
        List<IssueSummary> allIssues,
        List<String> notableComments,
        List<String> blockers,
        Integer totalIssueCount,
        Integer totalCommentCount,
        Integer totalChangelogCount,
        Instant assembledAt
) {
}
