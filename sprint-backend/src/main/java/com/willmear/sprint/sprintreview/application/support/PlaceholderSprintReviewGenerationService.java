package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import com.willmear.sprint.sprintreview.domain.service.SprintThemeExtractor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderSprintReviewGenerationService {

    private final SprintThemeExtractor sprintThemeExtractor;
    private final SprintReviewProperties sprintReviewProperties;

    public PlaceholderSprintReviewGenerationService(
            SprintThemeExtractor sprintThemeExtractor,
            SprintReviewProperties sprintReviewProperties
    ) {
        this.sprintThemeExtractor = sprintThemeExtractor;
        this.sprintReviewProperties = sprintReviewProperties;
    }

    public SprintReview generate(SprintContext context, SprintReviewGenerationInput input) {
        return new SprintReview(
                UUID.randomUUID(),
                context.workspaceId(),
                context.externalSprintId(),
                context.sprintName(),
                buildSummary(context),
                buildThemes(context),
                buildHighlights(context),
                buildBlockers(context),
                buildSpeakerNotes(context, input),
                Instant.now(),
                input.generationSource(),
                "GENERATED"
        );
    }

    public SprintSummary buildSummary(SprintContext context) {
        int highlightCount = Math.min(context.completedIssues().size(), sprintReviewProperties.maxHighlights());
        return new SprintSummary(
                "Sprint Review: " + context.sprintName(),
                "The sprint delivered " + context.completedIssues().size() + " completed issues across "
                        + context.totalIssueCount() + " tracked items.",
                "Delivery centered on " + highlightCount + " notable completed items.",
                "Quality work included " + context.bugFixes().size() + " bug-related items.",
                "The sprint ended with " + context.carriedOverIssues().size() + " carried-over issues."
        );
    }

    public List<SprintTheme> buildThemes(SprintContext context) {
        return sprintThemeExtractor.extract(context, sprintReviewProperties.maxThemes());
    }

    public List<SprintHighlight> buildHighlights(SprintContext context) {
        return context.completedIssues().stream()
                .limit(sprintReviewProperties.maxHighlights())
                .map(this::toHighlight)
                .toList();
    }

    public List<SprintBlocker> buildBlockers(SprintContext context) {
        return context.blockers().stream()
                .map(blocker -> new SprintBlocker("Delivery blocker", blocker, List.of(), "MEDIUM"))
                .toList();
    }

    public List<SpeakerNote> buildSpeakerNotes(SprintContext context, SprintReviewGenerationInput input) {
        return List.of(
                new SpeakerNote("Introduction", "Open with the sprint goal: " + safe(context.sprintGoal()), 1),
                new SpeakerNote("Delivery", "Call out " + context.completedIssues().size() + " completed issues and the top highlights.", 2),
                new SpeakerNote("Technical Work", "Mention " + context.technicalImprovements().size() + " technical improvement items.", 3),
                new SpeakerNote("Risks", "Note " + context.carriedOverIssues().size() + " carried-over issues and any blockers.", 4),
                new SpeakerNote("Wrap Up", "Close with audience=" + safe(input.audience()) + ", tone=" + safe(input.tone()) + ".", 5)
        );
    }

    private SprintHighlight toHighlight(IssueSummary issue) {
        String category = issue.bugFix() ? "BUGFIX" : issue.technicalWork() ? "IMPROVEMENT" : "FEATURE";
        return new SprintHighlight(
                issue.issueKey() + " - " + issue.summary(),
                issue.description() != null ? issue.description() : "Completed during the sprint.",
                List.of(issue.issueKey()),
                category
        );
    }

    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "default";
    }
}
