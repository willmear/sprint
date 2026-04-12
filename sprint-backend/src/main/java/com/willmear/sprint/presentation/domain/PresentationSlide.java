package com.willmear.sprint.presentation.domain;

import com.willmear.sprint.presentation.template.SlideTemplateType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PresentationSlide(
        UUID id,
        UUID deckId,
        Integer slideOrder,
        SlideType slideType,
        String title,
        List<String> bulletPoints,
        String bodyText,
        String speakerNotes,
        String sectionLabel,
        String backgroundColor,
        BackgroundStyleType backgroundStyleType,
        boolean showGrid,
        SlideLayoutType layoutType,
        SlideTemplateType templateType,
        List<PresentationSlideElement> elements,
        boolean hidden,
        Instant createdAt,
        Instant updatedAt
) {
}
