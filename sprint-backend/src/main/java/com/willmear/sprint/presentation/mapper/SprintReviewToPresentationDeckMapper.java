package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.presentation.domain.DeckStatus;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.PresentationSlide;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewToPresentationDeckMapper {

    public static final String SPRINT_REFERENCE_TYPE = "SPRINT";

    public PresentationDeck toDeck(Artifact artifact, SprintReview review) {
        List<PresentationSlide> slides = new ArrayList<>();
        slides.add(slide(null, 0, SlideType.TITLE, "Sprint Review: " + review.sprintName(), List.of(
                review.summary().title(),
                fallback(review.summary().overview(), review.sprintName())
        ), null, firstSpeakerNote(review), "Intro", SlideLayoutType.SECTION_SUMMARY, false));
        slides.add(slide(null, 1, SlideType.OVERVIEW, "Sprint overview", overviewBullets(review), review.summary().overview(),
                speakerNotesForSection(review, "intro", "overview"), "Overview", SlideLayoutType.TITLE_BODY_NOTES, false));
        slides.add(slide(null, 2, SlideType.THEMES, "Key themes", themeBullets(review.themes()), null,
                speakerNotesForSection(review, "theme"), "Themes", SlideLayoutType.TITLE_AND_BULLETS, false));
        slides.add(slide(null, 3, SlideType.HIGHLIGHTS, "Highlights", highlightBullets(review.highlights()), null,
                speakerNotesForSection(review, "highlight"), "Highlights", SlideLayoutType.TITLE_AND_BULLETS, false));
        slides.add(slide(null, 4, SlideType.BLOCKERS, "Blockers and risks", blockerBullets(review.blockers()), null,
                speakerNotesForSection(review, "block", "risk"), "Risks", SlideLayoutType.TITLE_AND_BULLETS, false));
        slides.add(slide(null, 5, SlideType.SPEAKER_NOTES, "Wrap-up", List.of(
                fallback(review.summary().outcomeSummary(), "Close with delivery outcomes and next steps.")
        ), null, joinSpeakerNotes(review), "Closing", SlideLayoutType.TITLE_BODY_NOTES, false));

        return new PresentationDeck(
                null,
                review.workspaceId(),
                SPRINT_REFERENCE_TYPE,
                review.externalSprintId().toString(),
                "Sprint Review Deck: " + review.sprintName(),
                fallback(review.summary().overview(), review.sprintName()),
                DeckStatus.DRAFT,
                slides,
                artifact.id(),
                null,
                null
        );
    }

    private PresentationSlide slide(
            UUID id,
            int order,
            SlideType slideType,
            String title,
            List<String> bulletPoints,
            String bodyText,
            String speakerNotes,
            String sectionLabel,
            SlideLayoutType layoutType,
            boolean hidden
    ) {
        return new PresentationSlide(
                id,
                null,
                order,
                slideType,
                title,
                bulletPoints,
                bodyText,
                speakerNotes,
                sectionLabel,
                layoutType,
                buildElements(title, bulletPoints, bodyText, sectionLabel, slideType),
                hidden,
                null,
                null
        );
    }

    private List<PresentationSlideElement> buildElements(
            String title,
            List<String> bulletPoints,
            String bodyText,
            String sectionLabel,
            SlideType slideType
    ) {
        List<PresentationSlideElement> elements = new ArrayList<>();
        int elementOrder = 0;
        if (sectionLabel != null && !sectionLabel.isBlank()) {
            elements.add(textElement(elementOrder++, SlideElementRole.SECTION_LABEL, sectionLabel, 96.0, 42.0, 420.0, 28.0, 14, true, false, TextAlignment.LEFT));
        }
        elements.add(textElement(
                elementOrder++,
                SlideElementRole.TITLE,
                title,
                96.0,
                slideType == SlideType.TITLE ? 120.0 : 74.0,
                1088.0,
                slideType == SlideType.TITLE ? 140.0 : 88.0,
                slideType == SlideType.TITLE ? 38 : 30,
                true,
                false,
                slideType == SlideType.TITLE ? TextAlignment.CENTER : TextAlignment.LEFT
        ));
        String bodyContent = composeBodyContent(bulletPoints, bodyText);
        if (!bodyContent.isBlank()) {
            elements.add(textElement(
                    elementOrder,
                    SlideElementRole.BODY,
                    bodyContent,
                    slideType == SlideType.TITLE ? 184.0 : 96.0,
                    slideType == SlideType.TITLE ? 300.0 : 190.0,
                    slideType == SlideType.TITLE ? 912.0 : 760.0,
                    slideType == SlideType.TITLE ? 210.0 : 310.0,
                    slideType == SlideType.TITLE ? 20 : 22,
                    false,
                    false,
                    slideType == SlideType.TITLE ? TextAlignment.CENTER : TextAlignment.LEFT
            ));
        }
        return elements;
    }

    private PresentationSlideElement textElement(
            int order,
            SlideElementRole role,
            String text,
            double x,
            double y,
            double width,
            double height,
            int fontSize,
            boolean bold,
            boolean italic,
            TextAlignment alignment
    ) {
        return new PresentationSlideElement(
                null,
                null,
                order,
                SlideElementType.TEXT_BOX,
                role,
                text,
                x,
                y,
                width,
                height,
                "Aptos",
                fontSize,
                bold,
                italic,
                alignment,
                null,
                null
        );
    }

    private String composeBodyContent(List<String> bulletPoints, String bodyText) {
        List<String> sections = new ArrayList<>();
        if (bodyText != null && !bodyText.isBlank()) {
            sections.add(bodyText);
        }
        if (bulletPoints != null && !bulletPoints.isEmpty()) {
            sections.add(bulletPoints.stream()
                    .filter(point -> point != null && !point.isBlank())
                    .map(point -> "• " + point)
                    .reduce((left, right) -> left + "\n" + right)
                    .orElse(""));
        }
        return sections.stream()
                .filter(section -> !section.isBlank())
                .reduce((left, right) -> left + "\n\n" + right)
                .orElse("");
    }

    private List<String> overviewBullets(SprintReview review) {
        List<String> bullets = new ArrayList<>();
        bullets.add(fallback(review.summary().deliverySummary(), "No delivery summary recorded."));
        bullets.add(fallback(review.summary().qualitySummary(), "No quality summary recorded."));
        bullets.add(fallback(review.summary().outcomeSummary(), "No outcome summary recorded."));
        return bullets;
    }

    private List<String> themeBullets(List<SprintTheme> themes) {
        if (themes.isEmpty()) {
            return List.of("No themes were identified in the generated review.");
        }
        return themes.stream()
                .map(theme -> theme.name() + ": " + theme.description() + issueSuffix(theme.relatedIssueKeys()))
                .toList();
    }

    private List<String> highlightBullets(List<SprintHighlight> highlights) {
        if (highlights.isEmpty()) {
            return List.of("No highlights were captured.");
        }
        return highlights.stream()
                .map(highlight -> highlight.title() + ": " + highlight.description() + issueSuffix(highlight.relatedIssueKeys()))
                .toList();
    }

    private List<String> blockerBullets(List<SprintBlocker> blockers) {
        if (blockers.isEmpty()) {
            return List.of("No blockers or risks were captured.");
        }
        return blockers.stream()
                .map(blocker -> blocker.title() + " (" + blocker.severity() + "): " + blocker.description() + issueSuffix(blocker.relatedIssueKeys()))
                .toList();
    }

    private String issueSuffix(List<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return "";
        }
        return " [" + String.join(", ", issueKeys) + "]";
    }

    private String firstSpeakerNote(SprintReview review) {
        return review.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String speakerNotesForSection(SprintReview review, String... keywords) {
        return review.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
                .filter(note -> containsKeyword(note.section(), keywords))
                .map(SpeakerNote::note)
                .findFirst()
                .orElse(null);
    }

    private String joinSpeakerNotes(SprintReview review) {
        return review.speakerNotes().stream()
                .sorted((left, right) -> Integer.compare(left.displayOrder(), right.displayOrder()))
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

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
