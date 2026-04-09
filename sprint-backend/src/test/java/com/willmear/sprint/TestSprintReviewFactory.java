package com.willmear.sprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import com.willmear.sprint.sprintreview.domain.model.IssueCommentSummary;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class TestSprintReviewFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestSprintReviewFactory() {
    }

    public static SprintContext context(UUID workspaceId, UUID connectionId, Long sprintId) {
        return new SprintContext(
                workspaceId,
                connectionId,
                sprintId,
                "Sprint " + sprintId,
                "Goal",
                "ACTIVE",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-14T00:00:00Z"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                0,
                0,
                0,
                Instant.now()
        );
    }

    public static SprintContext contextWithIssues() {
        UUID workspaceId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        IssueSummary feature = new IssueSummary("SPR-1", "Feature", "Delivered feature", "Story", "Done", "High", "Alex", 5, false, false, List.of());
        IssueSummary bug = new IssueSummary("SPR-2", "Bug fix", "Fixed defect", "Bug", "Resolved", "Medium", "Sam", 3, true, false, List.of());
        IssueSummary technical = new IssueSummary("SPR-3", "Tech debt", "Cleanup", "Task", "In Progress", "Low", "Chris", 2, false, true, List.of(
                new IssueCommentSummary("Sam", "Waiting for QA sign-off", Instant.parse("2026-01-09T10:15:00Z"))
        ));
        IssueSummary carry = new IssueSummary("SPR-4", "Dependency", "Waiting", "Story", "Blocked", "High", "Jamie", 8, false, false, List.of(
                new IssueCommentSummary("Alex", "Blocked on upstream dependency", Instant.parse("2026-01-10T09:00:00Z"))
        ));
        return new SprintContext(
                workspaceId,
                connectionId,
                42L,
                "Sprint 42",
                "Ship value",
                "ACTIVE",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-14T00:00:00Z"),
                List.of(feature, bug),
                List.of(technical),
                List.of(carry),
                List.of(bug),
                List.of(technical),
                List.of(feature, bug, technical, carry),
                List.of("Blocked on upstream dependency", "Waiting for QA sign-off"),
                List.of("SPR-4: carried over with status Blocked"),
                4,
                2,
                1,
                Instant.now()
        );
    }

    public static SprintDataBundle bundleWithJiraData() {
        UUID workspaceId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        return new SprintDataBundle(
                workspaceId,
                connectionId,
                new JiraSprint(42L, workspaceId, connectionId, 7L, "Sprint 42", "Ship value", "ACTIVE",
                        Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-14T00:00:00Z"), null, Instant.now()),
                List.of(
                        new JiraIssue(workspaceId, connectionId, 42L, "SPR-1", "1", "Feature", "Delivered feature", "Story", "Done", "High", "Alex", "Pat", 5, Instant.now(), Instant.now()),
                        new JiraIssue(workspaceId, connectionId, 42L, "SPR-2", "2", "Bug fix", "Fixed defect", "Bug", "Resolved", "Medium", "Sam", "Pat", 3, Instant.now(), Instant.now()),
                        new JiraIssue(workspaceId, connectionId, 42L, "SPR-3", "3", "Tech debt", "Cleanup", "Task", "In Progress", "Low", "Chris", "Pat", 2, Instant.now(), Instant.now()),
                        new JiraIssue(workspaceId, connectionId, 42L, "SPR-4", "4", "Dependency", "Waiting", "Story", "Blocked", "High", "Jamie", "Pat", 8, Instant.now(), Instant.now())
                ),
                List.of(
                        new JiraComment(workspaceId, "c1", "SPR-4", "Alex", "Blocked on upstream dependency", Instant.now(), Instant.now()),
                        new JiraComment(workspaceId, "c2", "SPR-3", "Sam", "Waiting for QA sign-off", Instant.now(), Instant.now())
                ),
                List.of(
                        new JiraChangelogEvent(workspaceId, "h1", "SPR-4", "status", "In Progress", "Blocked", Instant.now(), "Alex")
                )
        );
    }

    public static SprintReview review(UUID workspaceId, Long sprintId, String source) {
        return new SprintReview(
                UUID.randomUUID(),
                workspaceId,
                sprintId,
                "Sprint " + sprintId,
                new SprintSummary("Sprint Review", "Overview", "Delivery", "Quality", "Outcome"),
                List.of(theme()),
                List.of(highlight()),
                List.of(blocker()),
                List.of(note()),
                Instant.now(),
                source,
                "GENERATED"
        );
    }

    public static SprintReview reviewWithHighlight() {
        return review(UUID.randomUUID(), 42L, "DIRECT");
    }

    public static SprintReview reviewMissingOverview() {
        SprintReview review = reviewWithHighlight();
        return new SprintReview(review.id(), review.workspaceId(), review.externalSprintId(), review.sprintName(),
                new SprintSummary("Sprint Review", "", "Delivery", "Quality", "Outcome"), review.themes(), review.highlights(),
                review.blockers(), review.speakerNotes(), review.generatedAt(), review.generationSource(), review.status());
    }

    public static SprintReview reviewMissingHighlights() {
        SprintReview review = reviewWithHighlight();
        return new SprintReview(review.id(), review.workspaceId(), review.externalSprintId(), review.sprintName(),
                review.summary(), review.themes(), List.of(), review.blockers(), review.speakerNotes(),
                review.generatedAt(), review.generationSource(), review.status());
    }

    public static SprintTheme theme() {
        return new SprintTheme("Theme", "Description", List.of("SPR-1"));
    }

    public static SprintHighlight highlight() {
        return new SprintHighlight("Highlight", "Delivered", List.of("SPR-1"), "FEATURE");
    }

    public static SprintBlocker blocker() {
        return new SprintBlocker("Blocker", "Dependency", List.of("SPR-2"), "MEDIUM");
    }

    public static SpeakerNote note() {
        return new SpeakerNote("Intro", "Open strongly", 1);
    }

    public static SprintReviewGenerationInput input(String source) {
        return new SprintReviewGenerationInput(UUID.randomUUID(), 42L, true, true, "", "", source);
    }

    public static Artifact artifact() {
        UUID workspaceId = UUID.randomUUID();
        var structuredContent = OBJECT_MAPPER.createObjectNode();
        structuredContent.put("sprintName", "Sprint 42");
        structuredContent.putObject("summary").put("title", "Sprint Review").put("overview", "Overview");
        return new Artifact(
                UUID.randomUUID(),
                workspaceId,
                ArtifactType.SPRINT_REVIEW,
                ArtifactStatus.GENERATED,
                "SPRINT",
                "42",
                structuredContent,
                "# Sprint Review",
                "Sprint Review",
                "Overview",
                "DIRECT",
                "v1",
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
    }
}
