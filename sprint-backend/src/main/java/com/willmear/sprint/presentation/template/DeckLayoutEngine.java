package com.willmear.sprint.presentation.template;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.presentation.domain.DeckStatus;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.PresentationSlide;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.theme.application.PresentationThemeService;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import com.willmear.sprint.presentationplan.domain.LayoutHint;
import com.willmear.sprint.presentationplan.domain.PlannedSlide;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.presentationplan.domain.SlideBlock;
import com.willmear.sprint.presentationplan.domain.SlideBlockType;
import com.willmear.sprint.presentationplan.domain.SlideIntent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DeckLayoutEngine {

    private static final String DEFAULT_FONT_FAMILY = "Aptos";

    private final SlideTemplateRegistry slideTemplateRegistry;
    private final PresentationThemeService presentationThemeService;

    public DeckLayoutEngine(
            SlideTemplateRegistry slideTemplateRegistry,
            PresentationThemeService presentationThemeService
    ) {
        this.slideTemplateRegistry = slideTemplateRegistry;
        this.presentationThemeService = presentationThemeService;
    }

    public PresentationDeck layout(Artifact artifact, PresentationPlan presentationPlan) {
        return layout(artifact, presentationPlan, null);
    }

    public PresentationDeck layout(Artifact artifact, PresentationPlan presentationPlan, String themeId) {
        PresentationTheme theme = presentationThemeService.resolveTheme(themeId);
        List<PresentationSlide> slides = new ArrayList<>();
        Set<SlideIntent> dividerSections = new HashSet<>();
        for (PlannedSlide plannedSlide : presentationPlan.slides().stream()
                .sorted(Comparator.comparing(PlannedSlide::slideOrder))
                .toList()) {
            if (shouldInsertSectionDivider(plannedSlide, dividerSections)) {
                slides.add(toSlide(sectionDividerFor(plannedSlide), slideTemplateRegistry.get(SlideTemplateType.SECTION_DIVIDER), theme));
                dividerSections.add(plannedSlide.slideIntent());
            }
            slides.addAll(layoutSlides(plannedSlide, theme));
        }

        List<PresentationSlide> orderedSlides = new ArrayList<>();
        for (int index = 0; index < slides.size(); index++) {
            PresentationSlide slide = slides.get(index);
            orderedSlides.add(new PresentationSlide(
                    slide.id(),
                    slide.deckId(),
                    index,
                    slide.slideType(),
                    slide.title(),
                    slide.bulletPoints(),
                    slide.bodyText(),
                    slide.speakerNotes(),
                    slide.sectionLabel(),
                    slide.backgroundColor(),
                    slide.backgroundStyleType(),
                    slide.showGrid(),
                    slide.layoutType(),
                    slide.templateType(),
                    slide.elements(),
                    slide.hidden(),
                    slide.createdAt(),
                    slide.updatedAt()
            ));
        }

        return new PresentationDeck(
                null,
                presentationPlan.workspaceId(),
                presentationPlan.referenceType(),
                presentationPlan.referenceId(),
                presentationPlan.title(),
                presentationPlan.subtitle(),
                theme.themeId(),
                DeckStatus.DRAFT,
                orderedSlides,
                artifact.id(),
                null,
                null
        );
    }

    private List<PresentationSlide> layoutSlides(PlannedSlide plannedSlide, PresentationTheme theme) {
        SlideTemplate template = slideTemplateRegistry.get(resolveTemplateType(plannedSlide));
        List<PlannedSlide> fittedSlides = splitForTemplate(plannedSlide, template);
        List<PresentationSlide> slides = new ArrayList<>();
        for (PlannedSlide fittedSlide : fittedSlides) {
            slides.add(toSlide(fittedSlide, template, theme));
        }
        return slides;
    }

    private PresentationSlide toSlide(PlannedSlide plannedSlide, SlideTemplate template, PresentationTheme theme) {
        SlideContent content = SlideContent.from(plannedSlide);
        List<PresentationSlideElement> elements = new ArrayList<>();
        int order = 0;

        if (content.sectionLabel() != null) {
            elements.add(textElement(order++, template.slot("section"), content.sectionLabel(), 0, theme, true));
        }
        elements.add(textElement(order++, template.slot("title"), plannedSlide.title(), titleFontDelta(plannedSlide.title()), theme, false));

        switch (template.type()) {
            case TITLE_SLIDE -> {
                if (plannedSlide.subtitle() != null && !plannedSlide.subtitle().isBlank()) {
                    elements.add(textElement(order++, template.slot("subtitle"), plannedSlide.subtitle(), 0, theme, false));
                }
                if (content.primaryBody() != null) {
                    elements.add(textElement(order++, template.slot("body"), content.primaryBody(), 0, theme, false));
                }
            }
            case SECTION_DIVIDER -> {
                if (plannedSlide.subtitle() != null && hasSlot(template, "subtitle")) {
                    elements.add(textElement(order++, template.slot("subtitle"), plannedSlide.subtitle(), 0, theme, false));
                }
                if (content.footer() != null && hasSlot(template, "footer")) {
                    elements.add(textElement(order++, template.slot("footer"), content.footer(), 0, theme, false));
                }
            }
            case EXECUTIVE_SUMMARY, CALLOUT_SUMMARY, CLOSING_SUMMARY -> {
                String metricText = resolveMetricText(plannedSlide, content);
                if (metricText != null && hasSlot(template, "metric")) {
                    elements.add(textElement(order++, template.slot("metric"), metricText, 0, theme, false));
                }
                if (content.primaryBody() != null) {
                    elements.add(textElement(order++, template.slot("callout"), content.primaryBody(), bodyFontDelta(content.primaryBody(), 0), theme, false));
                }
                if (!content.bullets().isEmpty()) {
                    elements.add(textElement(order++, template.slot("body"), bulletText(content.bullets()), bodyFontDelta(null, content.bullets().size()), theme, false));
                }
                if (content.footer() != null && hasSlot(template, "footer")) {
                    elements.add(textElement(order++, template.slot("footer"), content.footer(), 0, theme, false));
                }
            }
            case BLOCKERS_RISKS -> {
                if (!content.bullets().isEmpty()) {
                    elements.add(textElement(order++, template.slot("body"), bulletText(content.bullets()), bodyFontDelta(null, content.bullets().size()), theme, false));
                }
                if (content.primaryBody() != null && hasSlot(template, "callout")) {
                    elements.add(textElement(order++, template.slot("callout"), content.primaryBody(), 0, theme, false));
                }
                String metricText = resolveMetricText(plannedSlide, content);
                if (metricText != null && hasSlot(template, "metric")) {
                    elements.add(textElement(order++, template.slot("metric"), metricText, 0, theme, false));
                }
                if (content.footer() != null && hasSlot(template, "footer")) {
                    elements.add(textElement(order++, template.slot("footer"), content.footer(), 0, theme, false));
                }
            }
            case TWO_COLUMN_HIGHLIGHTS -> {
                if (plannedSlide.subtitle() != null && hasSlot(template, "subtitle")) {
                    elements.add(textElement(order++, template.slot("subtitle"), plannedSlide.subtitle(), 0, theme, false));
                }
                List<String> bullets = content.bullets();
                int splitIndex = Math.min(template.slot("left").maxItems(), bullets.size());
                List<String> left = bullets.subList(0, splitIndex);
                List<String> right = bullets.subList(splitIndex, bullets.size());
                elements.add(textElement(order++, template.slot("left"), bulletText(left), bodyFontDelta(null, left.size()), theme, false));
                String rightText = right.isEmpty() ? fallback(content.primaryBody(), content.footer()) : bulletText(right);
                if (rightText != null && !rightText.isBlank()) {
                    elements.add(textElement(order++, template.slot("right"), rightText, bodyFontDelta(content.primaryBody(), right.size()), theme, false));
                }
                if (content.footer() != null && hasSlot(template, "footer")) {
                    elements.add(textElement(order++, template.slot("footer"), content.footer(), 0, theme, false));
                }
            }
        }

        return new PresentationSlide(
                null,
                null,
                plannedSlide.slideOrder(),
                template.type() == SlideTemplateType.SECTION_DIVIDER ? SlideType.CUSTOM : toSlideType(plannedSlide.slideIntent()),
                plannedSlide.title(),
                content.bullets(),
                content.primaryBody(),
                plannedSlide.speakerNotes(),
                content.sectionLabel(),
                theme.defaults().slideBackground(),
                com.willmear.sprint.presentation.domain.BackgroundStyleType.SOLID,
                false,
                template.layoutType(),
                template.type(),
                elements,
                false,
                null,
                null
        );
    }

    private List<PlannedSlide> splitForTemplate(PlannedSlide plannedSlide, SlideTemplate template) {
        SlideContent content = SlideContent.from(plannedSlide);
        if (content.bullets().size() <= template.maxBulletsPerSlide()) {
            return List.of(plannedSlide);
        }

        List<List<String>> chunks = chunk(content.bullets(), template.maxBulletsPerSlide());
        List<PlannedSlide> slides = new ArrayList<>();
        for (int index = 0; index < chunks.size(); index++) {
            List<SlideBlock> rebasedBlocks = new ArrayList<>();
            for (SlideBlock block : plannedSlide.blocks()) {
                if (block.blockType() == SlideBlockType.BULLETS) {
                    rebasedBlocks.add(new SlideBlock(
                            block.blockType(),
                            block.heading(),
                            chunks.get(index),
                            block.body(),
                            block.visualPriority()
                    ));
                } else if (index == 0 || block.blockType() != SlideBlockType.CALLOUT) {
                    rebasedBlocks.add(block);
                }
            }

            String title = index == 0 ? plannedSlide.title() : plannedSlide.title() + " (continued)";
            slides.add(new PlannedSlide(
                    plannedSlide.slideOrder() + index,
                    plannedSlide.slideIntent(),
                    title,
                    plannedSlide.subtitle(),
                    rebasedBlocks,
                    plannedSlide.layoutHint(),
                    plannedSlide.speakerNotes()
            ));
        }
        return slides;
    }

    private SlideTemplateType resolveTemplateType(PlannedSlide plannedSlide) {
        return switch (plannedSlide.slideIntent()) {
            case TITLE -> SlideTemplateType.TITLE_SLIDE;
            case OVERVIEW, METRICS -> SlideTemplateType.EXECUTIVE_SUMMARY;
            case THEMES -> SlideTemplateType.TWO_COLUMN_HIGHLIGHTS;
            case HIGHLIGHTS -> contentLooksGrouped(plannedSlide)
                    ? SlideTemplateType.TWO_COLUMN_HIGHLIGHTS
                    : SlideTemplateType.CALLOUT_SUMMARY;
            case BLOCKERS -> SlideTemplateType.BLOCKERS_RISKS;
            case CLOSING -> SlideTemplateType.CLOSING_SUMMARY;
        };
    }

    private boolean shouldInsertSectionDivider(PlannedSlide plannedSlide, Set<SlideIntent> dividerSections) {
        return switch (plannedSlide.slideIntent()) {
            case THEMES, HIGHLIGHTS, BLOCKERS -> !dividerSections.contains(plannedSlide.slideIntent());
            default -> false;
        };
    }

    private PlannedSlide sectionDividerFor(PlannedSlide plannedSlide) {
        String title = switch (plannedSlide.slideIntent()) {
            case THEMES -> "Delivery Themes";
            case HIGHLIGHTS -> "Key Highlights";
            case BLOCKERS -> "Risks And Blockers";
            default -> plannedSlide.title();
        };
        String subtitle = fallback(plannedSlide.subtitle(), SlideContent.from(plannedSlide).primaryBody());
        return new PlannedSlide(
                plannedSlide.slideOrder(),
                plannedSlide.slideIntent(),
                title,
                subtitle,
                List.of(
                        new SlideBlock(SlideBlockType.SECTION_LABEL, null, List.of(), "Section", null),
                        new SlideBlock(SlideBlockType.SUBTITLE, null, List.of(), subtitle, null)
                ),
                LayoutHint.TITLE_ONLY,
                null
        );
    }

    private boolean contentLooksGrouped(PlannedSlide plannedSlide) {
        SlideContent content = SlideContent.from(plannedSlide);
        return content.bullets().size() >= 4 || plannedSlide.layoutHint() == LayoutHint.TWO_COLUMN;
    }

    private String resolveMetricText(PlannedSlide plannedSlide, SlideContent content) {
        if (plannedSlide.subtitle() != null && !plannedSlide.subtitle().isBlank()) {
            return plannedSlide.subtitle();
        }
        if (content.sectionLabel() != null && !content.sectionLabel().isBlank()) {
            return content.sectionLabel();
        }
        if (!content.bullets().isEmpty()) {
            return content.bullets().size() + " points";
        }
        return null;
    }

    private PresentationSlideElement textElement(
            int order,
            SlideSlot slot,
            String text,
            int fontDelta,
            PresentationTheme theme,
            boolean sectionSlot
    ) {
        int fontSize = Math.max(slot.minFontSize(), resolveThemeFontSize(slot, theme, sectionSlot) + fontDelta);
        return new PresentationSlideElement(
                null,
                null,
                order,
                SlideElementType.TEXT_BOX,
                slot.role(),
                text,
                slot.x(),
                slot.y(),
                slot.width(),
                slot.height(),
                order,
                null,
                null,
                null,
                null,
                null,
                resolveFontFamily(slot, theme),
                fontSize,
                slot.bold(),
                slot.italic(),
                false,
                slot.alignment(),
                null,
                false,
                null,
                null
        );
    }

    private int resolveThemeFontSize(SlideSlot slot, PresentationTheme theme, boolean sectionSlot) {
        if (slot.role() == SlideElementRole.TITLE) {
            return theme.typography().titleFontSize();
        }
        if (slot.role() == SlideElementRole.SUBTITLE || slot.role() == SlideElementRole.METRIC) {
            return theme.typography().subtitleFontSize();
        }
        if (sectionSlot || slot.role() == SlideElementRole.FOOTER) {
            return theme.typography().smallFontSize();
        }
        return theme.typography().bodyFontSize();
    }

    private String resolveFontFamily(SlideSlot slot, PresentationTheme theme) {
        if (slot.role() == SlideElementRole.TITLE) {
            return fallback(theme.typography().titleFontFamily(), DEFAULT_FONT_FAMILY);
        }
        return fallback(theme.typography().bodyFontFamily(), DEFAULT_FONT_FAMILY);
    }

    private int titleFontDelta(String title) {
        if (title == null) {
            return 0;
        }
        if (title.length() > 70) {
            return -6;
        }
        if (title.length() > 50) {
            return -3;
        }
        return 0;
    }

    private int bodyFontDelta(String body, int bulletCount) {
        if (bulletCount >= 5) {
            return -3;
        }
        if (bulletCount >= 4 || (body != null && body.length() > 240)) {
            return -2;
        }
        if (body != null && body.length() > 180) {
            return -1;
        }
        return 0;
    }

    private boolean hasSlot(SlideTemplate template, String key) {
        return template.slots().stream().anyMatch(slot -> slot.key().equals(key));
    }

    private String bulletText(List<String> bullets) {
        return bullets.stream()
                .filter(bullet -> bullet != null && !bullet.isBlank())
                .map(bullet -> "• " + bullet)
                .collect(Collectors.joining("\n"));
    }

    private List<List<String>> chunk(List<String> items, int size) {
        List<List<String>> chunks = new ArrayList<>();
        for (int index = 0; index < items.size(); index += size) {
            chunks.add(items.subList(index, Math.min(index + size, items.size())));
        }
        return chunks;
    }

    private SlideType toSlideType(SlideIntent slideIntent) {
        return switch (slideIntent) {
            case TITLE -> SlideType.TITLE;
            case OVERVIEW, METRICS -> SlideType.OVERVIEW;
            case THEMES -> SlideType.THEMES;
            case HIGHLIGHTS -> SlideType.HIGHLIGHTS;
            case BLOCKERS -> SlideType.BLOCKERS;
            case CLOSING -> SlideType.SPEAKER_NOTES;
        };
    }

    private String fallback(String left, String right) {
        if (left != null && !left.isBlank()) {
            return left;
        }
        return right;
    }

    // TODO: add chart-specific template slots when chart elements exist in the deck model.
    // TODO: add image placeholder and crop-aware templates when image slide elements are supported.
    // TODO: add smarter visual balancing to redistribute text between callout and bullet slots.

    private record SlideContent(
            String sectionLabel,
            String primaryBody,
            List<String> bullets,
            String footer
    ) {
        private static SlideContent from(PlannedSlide plannedSlide) {
            String sectionLabel = null;
            String primaryBody = null;
            List<String> bullets = new ArrayList<>();
            List<String> supporting = new ArrayList<>();

            for (SlideBlock block : plannedSlide.blocks()) {
                if (block.blockType() == SlideBlockType.SECTION_LABEL) {
                    sectionLabel = fallbackValue(block.body(), block.heading());
                    continue;
                }
                if (block.blockType() == SlideBlockType.CALLOUT && primaryBody == null) {
                    primaryBody = block.heading() == null || block.heading().isBlank()
                            ? block.body()
                            : block.heading() + "\n" + block.body();
                    continue;
                }
                if (block.blockType() == SlideBlockType.BULLETS) {
                    bullets.addAll(block.items());
                    continue;
                }
                if (block.blockType() == SlideBlockType.TITLE || block.blockType() == SlideBlockType.SUBTITLE) {
                    continue;
                }
                String body = fallbackValue(block.body(), block.heading());
                if (body != null && !body.isBlank()) {
                    supporting.add(body);
                }
            }

            String footer = supporting.isEmpty() ? null : String.join(" • ", supporting);
            return new SlideContent(sectionLabel, primaryBody, bullets, footer);
        }

        private static String fallbackValue(String left, String right) {
            if (left != null && !left.isBlank()) {
                return left;
            }
            return right;
        }
    }
}
