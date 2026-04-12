package com.willmear.sprint.presentationplan.mapper;

import com.willmear.sprint.presentationplan.domain.LayoutHint;
import com.willmear.sprint.presentationplan.domain.PlannedSlide;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.presentationplan.domain.SlideBlock;
import com.willmear.sprint.presentationplan.domain.SlideBlockType;
import com.willmear.sprint.presentationplan.domain.SlideIntent;
import com.willmear.sprint.presentationplan.domain.VisualPriority;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewToPresentationPlanMapper {

    private static final int MAX_THEMES_PER_SLIDE = 4;
    private static final int MAX_HIGHLIGHTS_PER_SLIDE = 4;
    private static final int MAX_BLOCKERS_PER_SLIDE = 4;
    private static final int MAX_BULLET_LENGTH = 92;
    private static final int MAX_CALLOUT_LENGTH = 140;
    private static final int MAX_NOTES_LENGTH = 360;
    private static final int MAX_PLAN_TITLE_LENGTH = 60;

    public PresentationPlan toPlan(UUID workspaceId, String referenceType, String referenceId, SprintReview review) {
        List<PlannedSlide> plannedSlides = new ArrayList<>();

        plannedSlides.add(planTitleSlide(review));
        plannedSlides.add(planOverviewSlide(review));
        plannedSlides.addAll(planThemesSlides(review));
        plannedSlides.addAll(planHighlightsSlides(review));
        plannedSlides.addAll(planBlockerSlides(review));
        plannedSlides.add(planClosingSlide(review));

        List<PlannedSlide> orderedSlides = new ArrayList<>();
        for (int index = 0; index < plannedSlides.size(); index++) {
            PlannedSlide slide = plannedSlides.get(index);
            orderedSlides.add(new PlannedSlide(
                    index,
                    slide.slideIntent(),
                    concisePlanTitle(slide.title()),
                    blankToNull(slide.subtitle()),
                    slide.blocks(),
                    slide.layoutHint(),
                    blankToNull(slide.speakerNotes())
            ));
        }

        return new PresentationPlan(
                workspaceId,
                referenceType,
                referenceId,
                "Sprint Review Deck: " + review.sprintName(),
                fallback(shortSentence(review.summary().overview()), review.sprintName()),
                orderedSlides,
                Instant.now()
        );
    }

    private PlannedSlide planTitleSlide(SprintReview review) {
        String sprintTitle = concisePlanTitle(review.sprintName());
        String subtitle = shortSentence(fallback(review.summary().title(), review.summary().overview()));
        return new PlannedSlide(
                0,
                SlideIntent.TITLE,
                sprintTitle,
                subtitle,
                List.of(
                        block(SlideBlockType.TITLE, null, List.of(), sprintTitle, VisualPriority.PRIMARY),
                        block(SlideBlockType.SUBTITLE, null, List.of(), subtitle, VisualPriority.SECONDARY)
                ),
                LayoutHint.TITLE_ONLY,
                detailedNote(firstSpeakerNote(review), review.summary().overview())
        );
    }

    private PlannedSlide planOverviewSlide(SprintReview review) {
        List<String> bullets = List.of(
                conciseBullet(review.summary().deliverySummary()),
                conciseBullet(review.summary().qualitySummary()),
                conciseBullet(review.summary().outcomeSummary())
        ).stream().filter(value -> value != null && !value.isBlank()).toList();

        return new PlannedSlide(
                1,
                SlideIntent.OVERVIEW,
                "Executive summary",
                "Sprint at a glance",
                List.of(
                        block(SlideBlockType.SECTION_LABEL, null, List.of(), "Overview", VisualPriority.SUPPORTING),
                        block(SlideBlockType.METRIC, null, List.of(), bullets.size() + " focus areas", VisualPriority.SECONDARY),
                        block(SlideBlockType.CALLOUT, "Takeaway", List.of(), strongestOverviewCallout(review), VisualPriority.PRIMARY),
                        block(SlideBlockType.BULLETS, "Today", trimBullets(bullets, 3), null, VisualPriority.SECONDARY)
                ),
                LayoutHint.SECTION_SUMMARY,
                detailedNote(speakerNotesForSection(review, "intro", "overview"), joinOverviewDetails(review))
        );
    }

    private List<PlannedSlide> planThemesSlides(SprintReview review) {
        List<SprintTheme> themes = review.themes();
        if (themes.isEmpty()) {
            return List.of(new PlannedSlide(
                    100,
                    SlideIntent.THEMES,
                    "Themes",
                    "Delivery themes",
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Themes", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Theme signal", List.of(), "No major delivery themes surfaced this sprint.", VisualPriority.PRIMARY)
                    ),
                    LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "theme"), "No major delivery themes were identified.")
            ));
        }

        List<PlannedSlide> slides = new ArrayList<>();
        List<List<SprintTheme>> chunks = chunk(themes, MAX_THEMES_PER_SLIDE);
        for (int index = 0; index < chunks.size(); index++) {
            List<SprintTheme> chunk = chunks.get(index);
            SprintTheme lead = chunk.getFirst();
            slides.add(new PlannedSlide(
                    100 + index,
                    SlideIntent.THEMES,
                    index == 0 ? "Key themes" : "More themes",
                    index == 0 ? "Where effort landed" : null,
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Themes", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Lead theme", List.of(), themeCallout(lead), VisualPriority.PRIMARY),
                            block(SlideBlockType.BULLETS, "Theme bullets", trimBullets(chunk.stream().map(this::themeBullet).toList(), MAX_THEMES_PER_SLIDE), null, VisualPriority.SECONDARY),
                            block(SlideBlockType.METRIC, null, List.of(), themes.size() + " themes", VisualPriority.SUPPORTING)
                    ),
                    chunks.size() > 1 ? LayoutHint.TWO_COLUMN : LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "theme"), joinThemeDetails(chunk))
            ));
        }
        return slides;
    }

    private List<PlannedSlide> planHighlightsSlides(SprintReview review) {
        List<SprintHighlight> highlights = review.highlights();
        if (highlights.isEmpty()) {
            return List.of(new PlannedSlide(
                    200,
                    SlideIntent.HIGHLIGHTS,
                    "Highlights",
                    "Wins and progress",
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Highlights", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Outcome", List.of(), "No standout highlights were captured for this sprint.", VisualPriority.PRIMARY)
                    ),
                    LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "highlight"), "No highlights were captured.")
            ));
        }

        List<PlannedSlide> slides = new ArrayList<>();
        List<List<SprintHighlight>> chunks = chunk(highlights, MAX_HIGHLIGHTS_PER_SLIDE);
        for (int index = 0; index < chunks.size(); index++) {
            List<SprintHighlight> chunk = chunks.get(index);
            SprintHighlight lead = strongestHighlight(chunk);
            slides.add(new PlannedSlide(
                    200 + index,
                    SlideIntent.HIGHLIGHTS,
                    index == 0 ? "Top wins" : "More wins",
                    index == 0 ? "What landed well" : null,
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Highlights", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Top outcome", List.of(), highlightCallout(lead), VisualPriority.PRIMARY),
                            block(SlideBlockType.BULLETS, "Wins", trimBullets(chunk.stream().map(this::highlightBullet).toList(), MAX_HIGHLIGHTS_PER_SLIDE), null, VisualPriority.SECONDARY),
                            block(SlideBlockType.METRIC, null, List.of(), highlights.size() + " highlights", VisualPriority.SUPPORTING)
                    ),
                    chunks.size() > 1 ? LayoutHint.TWO_COLUMN : LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "highlight"), joinHighlightDetails(chunk))
            ));
        }
        return slides;
    }

    private List<PlannedSlide> planBlockerSlides(SprintReview review) {
        List<SprintBlocker> blockers = review.blockers();
        if (blockers.isEmpty()) {
            return List.of(new PlannedSlide(
                    300,
                    SlideIntent.BLOCKERS,
                    "Risks",
                    "No active blockers",
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Risks", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Risk signal", List.of(), "No active blockers or risks were captured.", VisualPriority.PRIMARY),
                            block(SlideBlockType.CLOSING_NOTE, null, List.of(), "Keep momentum on delivery and follow-through.", VisualPriority.SUPPORTING)
                    ),
                    LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "block", "risk"), "No blockers or risks were captured.")
            ));
        }

        List<PlannedSlide> slides = new ArrayList<>();
        List<List<SprintBlocker>> chunks = chunk(blockers, MAX_BLOCKERS_PER_SLIDE);
        for (int index = 0; index < chunks.size(); index++) {
            List<SprintBlocker> chunk = chunks.get(index);
            SprintBlocker lead = highestRiskBlocker(chunk);
            slides.add(new PlannedSlide(
                    300 + index,
                    SlideIntent.BLOCKERS,
                    index == 0 ? "Open risks" : "More risks",
                    index == 0 ? "What needs attention" : null,
                    List.of(
                            block(SlideBlockType.SECTION_LABEL, null, List.of(), "Risks", VisualPriority.SUPPORTING),
                            block(SlideBlockType.CALLOUT, "Watch item", List.of(), blockerCallout(lead), VisualPriority.PRIMARY),
                            block(SlideBlockType.BULLETS, "Risk bullets", trimBullets(chunk.stream().map(this::blockerBullet).toList(), MAX_BLOCKERS_PER_SLIDE), null, VisualPriority.SECONDARY),
                            block(SlideBlockType.METRIC, null, List.of(), blockers.size() + " open risks", VisualPriority.SUPPORTING)
                    ),
                    LayoutHint.CALLOUT,
                    detailedNote(speakerNotesForSection(review, "block", "risk"), joinBlockerDetails(chunk))
            ));
        }
        return slides;
    }

    private PlannedSlide planClosingSlide(SprintReview review) {
        return new PlannedSlide(
                999,
                SlideIntent.CLOSING,
                "Next steps",
                "Close with outcomes",
                List.of(
                        block(SlideBlockType.SECTION_LABEL, null, List.of(), "Closing", VisualPriority.SUPPORTING),
                        block(SlideBlockType.CALLOUT, "Close on", List.of(), shortSentence(fallback(review.summary().outcomeSummary(), review.summary().deliverySummary())), VisualPriority.PRIMARY),
                        block(SlideBlockType.BULLETS, "Leave with", trimBullets(List.of(
                                conciseBullet(review.summary().deliverySummary()),
                                conciseBullet(review.summary().outcomeSummary()),
                                conciseBullet(review.summary().qualitySummary())
                        ).stream().filter(value -> value != null && !value.isBlank()).toList(), 3), null, VisualPriority.SECONDARY),
                        block(SlideBlockType.CLOSING_NOTE, null, List.of(), "Next steps and open follow-through live in the notes.", VisualPriority.SUPPORTING)
                ),
                LayoutHint.CALLOUT,
                detailedNote(joinSpeakerNotes(review), joinOverviewDetails(review))
        );
    }

    private SlideBlock block(
            SlideBlockType type,
            String heading,
            List<String> items,
            String body,
            VisualPriority visualPriority
    ) {
        return new SlideBlock(type, heading, items == null ? List.of() : items, body, visualPriority);
    }

    private <T> List<List<T>> chunk(List<T> items, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int index = 0; index < items.size(); index += chunkSize) {
            chunks.add(items.subList(index, Math.min(index + chunkSize, items.size())));
        }
        return chunks;
    }

    private SprintHighlight strongestHighlight(List<SprintHighlight> highlights) {
        return highlights.stream()
                .max(Comparator.comparingInt(highlight -> safeLength(highlight.description())))
                .orElse(highlights.getFirst());
    }

    private SprintBlocker highestRiskBlocker(List<SprintBlocker> blockers) {
        return blockers.stream()
                .sorted(Comparator.comparingInt(this::severityWeight).reversed())
                .findFirst()
                .orElse(blockers.getFirst());
    }

    private int severityWeight(SprintBlocker blocker) {
        String severity = blocker.severity() == null ? "" : blocker.severity().toUpperCase(Locale.ROOT);
        return switch (severity) {
            case "HIGH", "CRITICAL" -> 3;
            case "MEDIUM" -> 2;
            default -> 1;
        };
    }

    private String strongestOverviewCallout(SprintReview review) {
        return shortSentence(fallback(review.summary().outcomeSummary(), review.summary().overview()));
    }

    private String themeCallout(SprintTheme theme) {
        return shortSentence(theme.name() + ": " + theme.description());
    }

    private String highlightCallout(SprintHighlight highlight) {
        return shortSentence(highlight.title() + ": " + highlight.description());
    }

    private String blockerCallout(SprintBlocker blocker) {
        return shortSentence(blocker.title() + " (" + blocker.severity() + "): " + blocker.description());
    }

    private String themeBullet(SprintTheme theme) {
        return conciseBullet(theme.name() + " - " + theme.description() + issueSuffix(theme.relatedIssueKeys()));
    }

    private String highlightBullet(SprintHighlight highlight) {
        return conciseBullet(highlight.title() + " - " + highlight.description() + issueSuffix(highlight.relatedIssueKeys()));
    }

    private String blockerBullet(SprintBlocker blocker) {
        return conciseBullet(blocker.title() + " - " + blocker.description() + issueSuffix(blocker.relatedIssueKeys()));
    }

    private String joinThemeDetails(List<SprintTheme> themes) {
        return themes.stream()
                .map(theme -> theme.name() + ": " + theme.description() + issueSuffix(theme.relatedIssueKeys()))
                .reduce((left, right) -> left + "\n" + right)
                .orElse(null);
    }

    private String joinHighlightDetails(List<SprintHighlight> highlights) {
        return highlights.stream()
                .map(highlight -> highlight.title() + ": " + highlight.description() + issueSuffix(highlight.relatedIssueKeys()))
                .reduce((left, right) -> left + "\n" + right)
                .orElse(null);
    }

    private String joinBlockerDetails(List<SprintBlocker> blockers) {
        return blockers.stream()
                .map(blocker -> blocker.title() + " (" + blocker.severity() + "): " + blocker.description() + issueSuffix(blocker.relatedIssueKeys()))
                .reduce((left, right) -> left + "\n" + right)
                .orElse(null);
    }

    private String joinOverviewDetails(SprintReview review) {
        return List.of(
                        review.summary().overview(),
                        review.summary().deliverySummary(),
                        review.summary().qualitySummary(),
                        review.summary().outcomeSummary()
                ).stream()
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + "\n\n" + right)
                .orElse(null);
    }

    private String issueSuffix(List<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return "";
        }
        return " [" + String.join(", ", issueKeys) + "]";
    }

    private String firstSpeakerNote(SprintReview review) {
        return review.speakerNotes().stream()
                .sorted(Comparator.comparing(SpeakerNote::displayOrder))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String speakerNotesForSection(SprintReview review, String... keywords) {
        return review.speakerNotes().stream()
                .sorted(Comparator.comparing(SpeakerNote::displayOrder))
                .filter(note -> containsKeyword(note.section(), keywords))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String joinSpeakerNotes(SprintReview review) {
        return review.speakerNotes().stream()
                .sorted(Comparator.comparing(SpeakerNote::displayOrder))
                .map(note -> note.section() + ": " + note.note())
                .reduce((left, right) -> left + "\n\n" + right)
                .orElse(null);
    }

    private boolean containsKeyword(String section, String... keywords) {
        String normalized = section == null ? "" : section.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private List<String> trimBullets(List<String> bullets, int maxCount) {
        return bullets.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(this::conciseBullet)
                .limit(maxCount)
                .toList();
    }

    private String conciseBullet(String value) {
        String populated = fallback(value, null);
        if (populated == null) {
            return null;
        }
        String normalized = populated.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        return normalized.length() <= MAX_BULLET_LENGTH ? normalized : normalized.substring(0, MAX_BULLET_LENGTH - 3).trim() + "...";
    }

    private String shortSentence(String value) {
        String populated = fallback(value, null);
        if (populated == null) {
            return null;
        }
        String normalized = populated.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        return normalized.length() <= MAX_CALLOUT_LENGTH ? normalized : normalized.substring(0, MAX_CALLOUT_LENGTH - 3).trim() + "...";
    }

    private String detailedNote(String primary, String fallback) {
        String note = this.fallback(primary, fallback);
        if (note == null) {
            return null;
        }
        String normalized = note.replaceAll("\\n{3,}", "\n\n").trim();
        return normalized.length() <= MAX_NOTES_LENGTH ? normalized : normalized.substring(0, MAX_NOTES_LENGTH - 3).trim() + "...";
    }

    private String concisePlanTitle(String value) {
        String populated = fallback(value, "");
        return populated.length() <= MAX_PLAN_TITLE_LENGTH ? populated : populated.substring(0, MAX_PLAN_TITLE_LENGTH - 3).trim() + "...";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String fallback(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback == null || fallback.isBlank() ? null : fallback.trim();
        }
        return value.trim();
    }

    private int safeLength(String value) {
        return value == null ? 0 : value.length();
    }
}
