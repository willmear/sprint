package com.willmear.sprint.export.renderer;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.domain.SpeakerNotesExport;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class SpeakerNotesExportRenderer implements ExportRenderer {

    @Override
    public ExportFormat supports() {
        return ExportFormat.SPEAKER_NOTES;
    }

    @Override
    public ExportPayload render(SprintReview sprintReview) {
        SpeakerNotesExport speakerNotesExport = buildExport(sprintReview);
        return new ExportPayload(
                ExportFormat.SPEAKER_NOTES,
                fileName(sprintReview, "-speaker-notes.txt"),
                "text/plain",
                speakerNotesExport.body(),
                null,
                Instant.now()
        );
    }

    private SpeakerNotesExport buildExport(SprintReview sprintReview) {
        StringBuilder body = new StringBuilder();
        body.append(sprintReview.summary().title()).append("\n\n");
        appendSection(body, "Intro", sprintReview.summary().overview(), matchingSpeakerNotes(sprintReview, "intro", "overview"));
        appendSection(body, "Delivery highlights", sprintReview.summary().deliverySummary(), sprintReview.highlights().stream()
                .map(highlight -> highlight.title() + ": " + highlight.description())
                .toList());
        appendSection(body, "Technical quality and improvements", sprintReview.summary().qualitySummary(), matchingSpeakerNotes(sprintReview, "quality"));
        appendSection(body, "Risks and blockers", sprintReview.summary().outcomeSummary(), sprintReview.blockers().stream()
                .map(blocker -> blocker.title() + " (" + blocker.severity() + "): " + blocker.description())
                .toList());
        appendSection(body, "Closing summary", sprintReview.summary().outcomeSummary(), matchingSpeakerNotes(sprintReview, "close", "wrap", "summary"));
        return new SpeakerNotesExport(sprintReview.summary().title(), body.toString().trim());
    }

    private void appendSection(StringBuilder builder, String title, String summaryLine, List<String> supportingLines) {
        builder.append(title).append("\n");
        if (summaryLine != null && !summaryLine.isBlank()) {
            builder.append(summaryLine).append("\n");
        }
        List<String> nonBlankLines = supportingLines.stream()
                .filter(line -> line != null && !line.isBlank())
                .toList();
        if (nonBlankLines.isEmpty()) {
            builder.append("- No additional notes recorded.\n\n");
            return;
        }
        nonBlankLines.forEach(line -> builder.append("- ").append(line).append("\n"));
        builder.append("\n");
    }

    private List<String> matchingSpeakerNotes(SprintReview sprintReview, String... keywords) {
        return sprintReview.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .filter(note -> containsKeyword(note, keywords))
                .map(SpeakerNote::note)
                .toList();
    }

    private boolean containsKeyword(SpeakerNote note, String[] keywords) {
        String section = note.section().toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (section.contains(keyword)) {
                return true;
            }
        }
        return false;
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
