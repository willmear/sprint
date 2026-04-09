package com.willmear.sprint.ai.model;

import java.util.List;
import java.util.Map;

public record SprintReviewPromptInput(
        SprintMetadata sprint,
        PromptCounts counts,
        List<PromptIssue> issues,
        List<String> blockers,
        List<String> notableComments
) {

    public record SprintMetadata(
            Long sprintId,
            String sprintName,
            String sprintGoal,
            String sprintState,
            String sprintStartDate,
            String sprintEndDate
    ) {
    }

    public record PromptCounts(
            Integer totalIssues,
            Integer completedIssues,
            Integer inProgressIssues,
            Integer carriedOverIssues,
            Integer bugFixes,
            Integer technicalImprovements,
            Integer totalComments,
            Integer totalChangelogEvents,
            Map<String, Integer> statusCounts,
            Map<String, Integer> issueTypeCounts
    ) {
    }

    public record PromptIssue(
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
            List<PromptComment> comments
    ) {
    }

    public record PromptComment(
            String authorDisplayName,
            String body,
            String createdAtExternal
    ) {
    }
}
