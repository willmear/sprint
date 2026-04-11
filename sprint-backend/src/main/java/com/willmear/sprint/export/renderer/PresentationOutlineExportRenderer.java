package com.willmear.sprint.export.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.domain.PresentationOutline;
import com.willmear.sprint.export.domain.PresentationSlide;
import com.willmear.sprint.export.mapper.PresentationOutlineResponseMapper;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PresentationOutlineExportRenderer implements ExportRenderer {

    private final ObjectMapper objectMapper;
    private final PresentationOutlineResponseMapper presentationOutlineResponseMapper;

    public PresentationOutlineExportRenderer(
            ObjectMapper objectMapper,
            PresentationOutlineResponseMapper presentationOutlineResponseMapper
    ) {
        this.objectMapper = objectMapper;
        this.presentationOutlineResponseMapper = presentationOutlineResponseMapper;
    }

    @Override
    public ExportFormat supports() {
        return ExportFormat.PRESENTATION_OUTLINE;
    }

    @Override
    public ExportPayload render(SprintReview sprintReview) {
        PresentationOutline outline = buildOutline(sprintReview);
        return new ExportPayload(
                ExportFormat.PRESENTATION_OUTLINE,
                fileName(sprintReview, "-outline.json"),
                "application/json",
                renderOutlineText(outline),
                objectMapper.valueToTree(presentationOutlineResponseMapper.toResponse(outline)),
                Instant.now()
        );
    }

    private PresentationOutline buildOutline(SprintReview sprintReview) {
        List<PresentationSlide> slides = new ArrayList<>();
        slides.add(new PresentationSlide(
                1,
                "Sprint Review: " + sprintReview.sprintName(),
                List.of(
                        safeValue(sprintReview.summary().overview(), "No overview recorded."),
                        safeValue(sprintReview.summary().deliverySummary(), "No delivery summary recorded.")
                ),
                firstSpeakerNote(sprintReview)
        ));
        slides.add(new PresentationSlide(
                2,
                "Sprint Goal and Outcome",
                List.of(
                        safeValue(sprintReview.summary().qualitySummary(), "No quality summary recorded."),
                        safeValue(sprintReview.summary().outcomeSummary(), "No outcome summary recorded.")
                ),
                speakerNotesForSection(sprintReview, "overview")
        ));
        slides.add(new PresentationSlide(
                3,
                "Key Themes",
                sprintReview.themes().isEmpty()
                        ? List.of("No major delivery themes were identified.")
                        : sprintReview.themes().stream().map(this::themeBullet).toList(),
                speakerNotesForSection(sprintReview, "theme")
        ));
        slides.add(new PresentationSlide(
                4,
                "Highlights",
                sprintReview.highlights().isEmpty()
                        ? List.of("No highlights were captured.")
                        : sprintReview.highlights().stream().map(this::highlightBullet).toList(),
                speakerNotesForSection(sprintReview, "highlight")
        ));
        slides.add(new PresentationSlide(
                5,
                "Blockers and Risks",
                sprintReview.blockers().isEmpty()
                        ? List.of("No blockers or risks were captured.")
                        : sprintReview.blockers().stream().map(this::blockerBullet).toList(),
                speakerNotesForSection(sprintReview, "blocker")
        ));
        slides.add(new PresentationSlide(
                6,
                "Wrap-up",
                List.of(safeValue(sprintReview.summary().outcomeSummary(), "No closing summary recorded.")),
                wrapUpSpeakerNotes(sprintReview)
        ));
        return new PresentationOutline("Sprint Review: " + sprintReview.sprintName(), slides);
    }

    private String renderOutlineText(PresentationOutline outline) {
        StringBuilder builder = new StringBuilder();
        builder.append(outline.title()).append("\n\n");
        for (PresentationSlide slide : outline.slides()) {
            builder.append("Slide ").append(slide.slideNumber()).append(" - ").append(slide.title()).append("\n");
            slide.bulletPoints().forEach(point -> builder.append("- ").append(point).append("\n"));
            if (slide.speakerNotes() != null && !slide.speakerNotes().isBlank()) {
                builder.append("Speaker notes: ").append(slide.speakerNotes()).append("\n");
            }
            builder.append("\n");
        }
        return builder.toString().trim();
    }

    private String themeBullet(SprintTheme theme) {
        return theme.name() + ": " + theme.description() + issueSuffix(theme.relatedIssueKeys());
    }

    private String highlightBullet(SprintHighlight highlight) {
        return highlight.title() + ": " + highlight.description() + issueSuffix(highlight.relatedIssueKeys());
    }

    private String blockerBullet(SprintBlocker blocker) {
        return blocker.title() + " (" + blocker.severity() + "): " + blocker.description() + issueSuffix(blocker.relatedIssueKeys());
    }

    private String issueSuffix(List<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return "";
        }
        return " [" + String.join(", ", issueKeys) + "]";
    }

    private String firstSpeakerNote(SprintReview sprintReview) {
        return sprintReview.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String speakerNotesForSection(SprintReview sprintReview, String sectionKeyword) {
        return sprintReview.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .filter(note -> note.section().toLowerCase(Locale.ROOT).contains(sectionKeyword))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String wrapUpSpeakerNotes(SprintReview sprintReview) {
        return sprintReview.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .reduce((left, right) -> right)
                .map(SpeakerNote::note)
                .orElse(sprintReview.summary().outcomeSummary());
    }

    private String safeValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
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
