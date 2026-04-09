package com.willmear.sprint.sprintreview.domain.service;

import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.sprintreview.domain.model.IssueCommentSummary;
import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SprintContextAssembler {

    public SprintContext assemble(SprintDataBundle bundle) {
        Map<String, List<IssueCommentSummary>> commentsByIssueKey = bundle.comments().stream()
                .collect(Collectors.groupingBy(
                        JiraComment::issueKey,
                        Collectors.mapping(this::toIssueCommentSummary, Collectors.toList())
                ));

        List<IssueSummary> allIssues = bundle.issues().stream()
                .map(issue -> toIssueSummary(issue, commentsByIssueKey.getOrDefault(issue.issueKey(), List.of())))
                .toList();
        List<IssueSummary> completedIssues = allIssues.stream().filter(issue -> isCompleted(issue.status())).toList();
        List<IssueSummary> inProgressIssues = allIssues.stream().filter(issue -> isInProgress(issue.status())).toList();
        List<IssueSummary> carriedOverIssues = allIssues.stream()
                .filter(issue -> !isCompleted(issue.status()) && !isInProgress(issue.status()))
                .toList();
        List<IssueSummary> bugFixes = allIssues.stream().filter(IssueSummary::bugFix).toList();
        List<IssueSummary> technicalImprovements = allIssues.stream().filter(IssueSummary::technicalWork).toList();
        List<String> notableComments = bundle.comments().stream()
                .filter(comment -> comment.body() != null && !comment.body().isBlank())
                .sorted(Comparator.comparing(JiraComment::createdAtExternal, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(8)
                .map(comment -> comment.issueKey() + " " + safe(comment.authorDisplayName()) + ": " + comment.body())
                .toList();
        List<String> blockers = inferBlockers(carriedOverIssues, bundle.comments(), bundle.changelogEvents());

        return new SprintContext(
                bundle.workspaceId(),
                bundle.jiraConnectionId(),
                bundle.sprint().externalSprintId(),
                bundle.sprint().name(),
                bundle.sprint().goal(),
                bundle.sprint().state(),
                bundle.sprint().startDate(),
                bundle.sprint().endDate(),
                completedIssues,
                inProgressIssues,
                carriedOverIssues,
                bugFixes,
                technicalImprovements,
                allIssues,
                notableComments,
                blockers,
                allIssues.size(),
                bundle.comments().size(),
                bundle.changelogEvents().size(),
                Instant.now()
        );
    }

    private IssueSummary toIssueSummary(JiraIssue issue, List<IssueCommentSummary> comments) {
        String issueType = issue.issueType() != null ? issue.issueType() : "";
        String summary = issue.summary() != null ? issue.summary() : issue.issueKey();
        return new IssueSummary(
                issue.issueKey(),
                summary,
                issue.description(),
                issue.issueType(),
                issue.status(),
                issue.priority(),
                issue.assigneeDisplayName(),
                issue.storyPoints(),
                issueType.toLowerCase().contains("bug"),
                isTechnicalWork(issueType, summary),
                comments.stream()
                        .sorted(Comparator.comparing(IssueCommentSummary::createdAtExternal, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList()
        );
    }

    private IssueCommentSummary toIssueCommentSummary(JiraComment comment) {
        return new IssueCommentSummary(
                comment.authorDisplayName(),
                comment.body(),
                comment.createdAtExternal()
        );
    }

    private boolean isCompleted(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.toLowerCase();
        return normalized.contains("done") || normalized.contains("complete") || normalized.contains("closed") || normalized.contains("resolved");
    }

    private boolean isInProgress(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.toLowerCase();
        return normalized.contains("progress") || normalized.contains("review") || normalized.contains("testing");
    }

    private boolean isTechnicalWork(String issueType, String summary) {
        String combined = ((issueType != null ? issueType : "") + " " + (summary != null ? summary : "")).toLowerCase();
        return combined.contains("tech") || combined.contains("debt") || combined.contains("chore") || combined.contains("task") || combined.contains("improvement");
    }

    private List<String> inferBlockers(
            List<IssueSummary> carriedOverIssues,
            List<JiraComment> comments,
            List<JiraChangelogEvent> changelogEvents
    ) {
        List<String> issueBlockers = carriedOverIssues.stream()
                .limit(3)
                .map(issue -> issue.issueKey() + ": carried over with status " + issue.status())
                .toList();
        List<String> commentBlockers = comments.stream()
                .map(JiraComment::body)
                .filter(body -> body != null && body.toLowerCase().matches(".*(blocked|dependency|waiting|risk).*"))
                .limit(3)
                .toList();
        List<String> changelogBlockers = changelogEvents.stream()
                .filter(event -> event.fieldName() != null && event.fieldName().equalsIgnoreCase("status"))
                .filter(event -> event.toValue() != null && event.toValue().toLowerCase().contains("blocked"))
                .map(event -> event.issueKey() + ": moved to " + event.toValue())
                .limit(3)
                .toList();
        return java.util.stream.Stream.of(issueBlockers, commentBlockers, changelogBlockers)
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }
}
