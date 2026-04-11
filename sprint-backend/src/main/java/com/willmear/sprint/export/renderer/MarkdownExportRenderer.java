package com.willmear.sprint.export.renderer;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MarkdownExportRenderer implements ExportRenderer {

    @Override
    public ExportFormat supports() {
        return ExportFormat.MARKDOWN;
    }

    @Override
    public ExportPayload render(SprintReview sprintReview) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Sprint Review: ").append(sprintReview.sprintName()).append("\n\n");

        appendParagraphSection(markdown, "Overview", List.of(
                sprintReview.summary().overview(),
                sprintReview.summary().deliverySummary(),
                sprintReview.summary().qualitySummary(),
                sprintReview.summary().outcomeSummary()
        ));
        appendThemes(markdown, sprintReview);
        appendHighlights(markdown, sprintReview);
        appendBlockers(markdown, sprintReview);
        appendSpeakerNotes(markdown, sprintReview);

        return new ExportPayload(
                ExportFormat.MARKDOWN,
                fileName(sprintReview, ".md"),
                "text/markdown",
                markdown.toString().trim(),
                null,
                Instant.now()
        );
    }

    private void appendParagraphSection(StringBuilder markdown, String title, List<String> paragraphs) {
        markdown.append("## ").append(title).append("\n\n");
        List<String> populatedParagraphs = paragraphs.stream()
                .filter(value -> value != null && !value.isBlank())
                .toList();
        if (populatedParagraphs.isEmpty()) {
            markdown.append("No summary was recorded for this section.\n\n");
            return;
        }
        populatedParagraphs.forEach(paragraph -> markdown.append(paragraph).append("\n\n"));
    }

    private void appendThemes(StringBuilder markdown, SprintReview sprintReview) {
        markdown.append("## Themes\n\n");
        if (sprintReview.themes().isEmpty()) {
            markdown.append("- No themes were captured.\n\n");
            return;
        }
        sprintReview.themes().forEach(theme -> {
            markdown.append("### ").append(theme.name()).append("\n");
            markdown.append(theme.description()).append("\n");
            if (!theme.relatedIssueKeys().isEmpty()) {
                markdown.append("Related issues: ").append(String.join(", ", theme.relatedIssueKeys())).append("\n");
            }
            markdown.append("\n");
        });
    }

    private void appendHighlights(StringBuilder markdown, SprintReview sprintReview) {
        markdown.append("## Highlights\n\n");
        if (sprintReview.highlights().isEmpty()) {
            markdown.append("- No highlights were captured.\n\n");
            return;
        }
        sprintReview.highlights().forEach(highlight -> markdown.append("- **")
                .append(highlight.title())
                .append("**: ")
                .append(highlight.description())
                .append(renderIssueSuffix(highlight.relatedIssueKeys()))
                .append("\n"));
        markdown.append("\n");
    }

    private void appendBlockers(StringBuilder markdown, SprintReview sprintReview) {
        markdown.append("## Blockers\n\n");
        if (sprintReview.blockers().isEmpty()) {
            markdown.append("- No blockers or risks were captured.\n\n");
            return;
        }
        sprintReview.blockers().forEach(blocker -> markdown.append("- **")
                .append(blocker.title())
                .append("** (")
                .append(blocker.severity())
                .append("): ")
                .append(blocker.description())
                .append(renderIssueSuffix(blocker.relatedIssueKeys()))
                .append("\n"));
        markdown.append("\n");
    }

    private void appendSpeakerNotes(StringBuilder markdown, SprintReview sprintReview) {
        markdown.append("## Speaker Notes\n\n");
        List<SpeakerNote> orderedNotes = sprintReview.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .toList();
        if (orderedNotes.isEmpty()) {
            markdown.append("1. No speaker notes were generated.\n");
            return;
        }
        int noteNumber = 1;
        for (SpeakerNote note : orderedNotes) {
            markdown.append(noteNumber++)
                    .append(". **")
                    .append(note.section())
                    .append("**: ")
                    .append(note.note())
                    .append("\n");
        }
    }

    private String renderIssueSuffix(List<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return "";
        }
        return " Related issues: " + issueKeys.stream().collect(Collectors.joining(", "));
    }

    private String fileName(SprintReview sprintReview, String suffix) {
        return "sprint-review-" + slugify(sprintReview.sprintName(), sprintReview.externalSprintId()) + suffix;
    }

    private String slugify(String sprintName, Long sprintId) {
        String base = (sprintName == null || sprintName.isBlank()) ? "sprint-" + sprintId : sprintName;
        return base.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
