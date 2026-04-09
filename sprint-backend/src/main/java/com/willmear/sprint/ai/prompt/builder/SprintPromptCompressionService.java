package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.model.SprintReviewPromptInput;
import com.willmear.sprint.config.SprintReviewAiProperties;
import com.willmear.sprint.sprintreview.domain.model.IssueCommentSummary;
import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SprintPromptCompressionService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final SprintReviewAiProperties sprintReviewAiProperties;

    public SprintPromptCompressionService(SprintReviewAiProperties sprintReviewAiProperties) {
        this.sprintReviewAiProperties = sprintReviewAiProperties;
    }

    public SprintReviewPromptInput buildPromptInput(SprintContext context) {
        List<IssueSummary> selectedIssues = selectIssues(context);
        return new SprintReviewPromptInput(
                new SprintReviewPromptInput.SprintMetadata(
                        context.externalSprintId(),
                        context.sprintName(),
                        context.sprintGoal(),
                        context.sprintState(),
                        formatInstant(context.sprintStartDate()),
                        formatInstant(context.sprintEndDate())
                ),
                new SprintReviewPromptInput.PromptCounts(
                        context.totalIssueCount(),
                        context.completedIssues().size(),
                        context.inProgressIssues().size(),
                        context.carriedOverIssues().size(),
                        context.bugFixes().size(),
                        context.technicalImprovements().size(),
                        context.totalCommentCount(),
                        context.totalChangelogCount(),
                        countByStatus(context.allIssues()),
                        countByIssueType(context.allIssues())
                ),
                selectedIssues.stream().map(this::toPromptIssue).toList(),
                context.blockers(),
                context.notableComments()
        );
    }

    private List<IssueSummary> selectIssues(SprintContext context) {
        int maxIssues = Math.max(1, sprintReviewAiProperties.maxIssuesInPrompt());
        return context.allIssues().stream()
                .sorted(Comparator
                        .comparingInt(this::issuePriorityBucket)
                        .thenComparing(IssueSummary::issueKey, Comparator.nullsLast(String::compareToIgnoreCase)))
                .limit(maxIssues)
                .toList();
    }

    private int issuePriorityBucket(IssueSummary issueSummary) {
        if (contextuallyIncomplete(issueSummary.status())) {
            return 0;
        }
        if (issueSummary.bugFix()) {
            return 1;
        }
        if (issueSummary.technicalWork()) {
            return 2;
        }
        return 3;
    }

    private boolean contextuallyIncomplete(String status) {
        if (status == null) {
            return true;
        }
        String normalized = status.toLowerCase();
        return !(normalized.contains("done") || normalized.contains("complete") || normalized.contains("closed") || normalized.contains("resolved"));
    }

    private SprintReviewPromptInput.PromptIssue toPromptIssue(IssueSummary issueSummary) {
        return new SprintReviewPromptInput.PromptIssue(
                issueSummary.issueKey(),
                issueSummary.summary(),
                truncate(issueSummary.description(), sprintReviewAiProperties.maxDescriptionChars()),
                issueSummary.issueType(),
                issueSummary.status(),
                issueSummary.priority(),
                issueSummary.assigneeDisplayName(),
                issueSummary.storyPoints(),
                issueSummary.bugFix(),
                issueSummary.technicalWork(),
                issueSummary.comments().stream()
                        .sorted(Comparator.comparing(IssueCommentSummary::createdAtExternal, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                        .limit(Math.max(0, sprintReviewAiProperties.maxCommentsPerIssue()))
                        .map(this::toPromptComment)
                        .toList()
        );
    }

    private SprintReviewPromptInput.PromptComment toPromptComment(IssueCommentSummary issueCommentSummary) {
        return new SprintReviewPromptInput.PromptComment(
                issueCommentSummary.authorDisplayName(),
                truncate(issueCommentSummary.body(), sprintReviewAiProperties.maxCommentChars()),
                formatInstant(issueCommentSummary.createdAtExternal())
        );
    }

    private Map<String, Integer> countByStatus(List<IssueSummary> issues) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (IssueSummary issue : issues) {
            String key = normalizeCounterValue(issue.status(), "UNKNOWN");
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }

    private Map<String, Integer> countByIssueType(List<IssueSummary> issues) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (IssueSummary issue : issues) {
            String key = normalizeCounterValue(issue.issueType(), "UNKNOWN");
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }

    private String normalizeCounterValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private String truncate(String value, int maxChars) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.length() <= maxChars) {
            return value;
        }
        return value.substring(0, Math.max(0, maxChars - 3)) + "...";
    }

    private String formatInstant(java.time.Instant value) {
        return value == null ? null : DATE_TIME_FORMATTER.format(value);
    }
}
