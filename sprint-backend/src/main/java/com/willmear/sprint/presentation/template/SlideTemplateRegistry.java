package com.willmear.sprint.presentation.template;

import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SlideTemplateRegistry {

    private final Map<SlideTemplateType, SlideTemplate> templates = new EnumMap<>(SlideTemplateType.class);

    public SlideTemplateRegistry() {
        register(titleSlide());
        register(sectionDivider());
        register(executiveSummary());
        register(twoColumnHighlights());
        register(calloutSummary());
        register(blockersRisks());
        register(closingSummary());
    }

    public SlideTemplate get(SlideTemplateType type) {
        SlideTemplate template = templates.get(type);
        if (template == null) {
            throw new IllegalArgumentException("No slide template registered for " + type);
        }
        return template;
    }

    private void register(SlideTemplate template) {
        templates.put(template.type(), template);
    }

    private SlideTemplate titleSlide() {
        return new SlideTemplate(
                SlideTemplateType.TITLE_SLIDE,
                SlideLayoutType.SECTION_SUMMARY,
                0,
                List.of(
                        new SlideSlot("title", SlideElementRole.TITLE, 150.0, 148.0, 980.0, 130.0, 38, 30, true, false, TextAlignment.CENTER, 0),
                        new SlideSlot("subtitle", SlideElementRole.SUBTITLE, 220.0, 292.0, 840.0, 64.0, 20, 16, false, false, TextAlignment.CENTER, 0),
                        new SlideSlot("body", SlideElementRole.CALLOUT, 220.0, 382.0, 840.0, 150.0, 19, 16, false, false, TextAlignment.CENTER, 0)
                )
        );
    }

    private SlideTemplate sectionDivider() {
        return new SlideTemplate(
                SlideTemplateType.SECTION_DIVIDER,
                SlideLayoutType.TITLE_ONLY,
                0,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 112.0, 102.0, 300.0, 34.0, 14, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 112.0, 188.0, 840.0, 128.0, 42, 32, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("subtitle", SlideElementRole.SUBTITLE, 112.0, 338.0, 760.0, 78.0, 22, 18, false, false, TextAlignment.LEFT, 0),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 112.0, 600.0, 860.0, 34.0, 14, 12, false, false, TextAlignment.LEFT, 0)
                )
        );
    }

    private SlideTemplate executiveSummary() {
        return new SlideTemplate(
                SlideTemplateType.EXECUTIVE_SUMMARY,
                SlideLayoutType.SECTION_SUMMARY,
                4,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 84.0, 40.0, 300.0, 28.0, 13, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 84.0, 84.0, 720.0, 88.0, 32, 26, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("metric", SlideElementRole.METRIC, 920.0, 88.0, 212.0, 96.0, 18, 16, true, false, TextAlignment.CENTER, 0),
                        new SlideSlot("callout", SlideElementRole.CALLOUT, 84.0, 208.0, 470.0, 210.0, 23, 18, false, false, TextAlignment.LEFT, 0),
                        new SlideSlot("body", SlideElementRole.BODY_BULLETS, 608.0, 208.0, 524.0, 262.0, 20, 16, false, false, TextAlignment.LEFT, 4),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 84.0, 560.0, 1048.0, 40.0, 15, 13, false, false, TextAlignment.LEFT, 0)
                )
        );
    }

    private SlideTemplate twoColumnHighlights() {
        return new SlideTemplate(
                SlideTemplateType.TWO_COLUMN_HIGHLIGHTS,
                SlideLayoutType.TITLE_BODY_NOTES,
                6,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 84.0, 40.0, 300.0, 28.0, 13, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 84.0, 84.0, 860.0, 82.0, 31, 25, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("subtitle", SlideElementRole.SUBTITLE, 84.0, 168.0, 760.0, 44.0, 18, 16, false, false, TextAlignment.LEFT, 0),
                        new SlideSlot("left", SlideElementRole.BODY_BULLETS, 96.0, 230.0, 470.0, 286.0, 20, 16, false, false, TextAlignment.LEFT, 3),
                        new SlideSlot("right", SlideElementRole.BODY_BULLETS, 664.0, 230.0, 470.0, 286.0, 20, 16, false, false, TextAlignment.LEFT, 3),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 96.0, 548.0, 1038.0, 40.0, 15, 13, false, false, TextAlignment.LEFT, 0)
                )
        );
    }

    private SlideTemplate calloutSummary() {
        return new SlideTemplate(
                SlideTemplateType.CALLOUT_SUMMARY,
                SlideLayoutType.TITLE_BODY_NOTES,
                4,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 84.0, 40.0, 300.0, 28.0, 13, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 84.0, 84.0, 900.0, 82.0, 31, 25, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("callout", SlideElementRole.CALLOUT, 96.0, 220.0, 428.0, 250.0, 22, 18, false, false, TextAlignment.LEFT, 0),
                        new SlideSlot("body", SlideElementRole.BODY_BULLETS, 588.0, 220.0, 544.0, 274.0, 20, 16, false, false, TextAlignment.LEFT, 4),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 96.0, 544.0, 1036.0, 40.0, 15, 13, false, false, TextAlignment.LEFT, 0)
                )
        );
    }

    private SlideTemplate blockersRisks() {
        return new SlideTemplate(
                SlideTemplateType.BLOCKERS_RISKS,
                SlideLayoutType.TITLE_BODY_NOTES,
                4,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 84.0, 40.0, 300.0, 28.0, 13, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 84.0, 84.0, 880.0, 84.0, 31, 25, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("callout", SlideElementRole.CALLOUT, 906.0, 96.0, 226.0, 126.0, 17, 15, true, false, TextAlignment.CENTER, 0),
                        new SlideSlot("body", SlideElementRole.BODY_BULLETS, 96.0, 222.0, 724.0, 286.0, 20, 16, false, false, TextAlignment.LEFT, 4),
                        new SlideSlot("metric", SlideElementRole.METRIC, 856.0, 254.0, 276.0, 112.0, 18, 15, true, false, TextAlignment.CENTER, 0),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 96.0, 548.0, 1036.0, 40.0, 15, 13, false, false, TextAlignment.LEFT, 0)
                )
        );
    }

    private SlideTemplate closingSummary() {
        return new SlideTemplate(
                SlideTemplateType.CLOSING_SUMMARY,
                SlideLayoutType.SECTION_SUMMARY,
                3,
                List.of(
                        new SlideSlot("section", SlideElementRole.SECTION_LABEL, 84.0, 40.0, 300.0, 28.0, 13, 12, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("title", SlideElementRole.TITLE, 84.0, 84.0, 860.0, 86.0, 32, 26, true, false, TextAlignment.LEFT, 0),
                        new SlideSlot("callout", SlideElementRole.CALLOUT, 96.0, 214.0, 596.0, 196.0, 24, 18, false, false, TextAlignment.LEFT, 0),
                        new SlideSlot("body", SlideElementRole.BODY_BULLETS, 742.0, 214.0, 390.0, 220.0, 19, 16, false, false, TextAlignment.LEFT, 3),
                        new SlideSlot("footer", SlideElementRole.FOOTER, 96.0, 548.0, 1036.0, 40.0, 15, 13, false, false, TextAlignment.LEFT, 0)
                )
        );
    }
}
