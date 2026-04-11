package com.willmear.sprint.presentation.domain;

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
        SlideLayoutType layoutType,
        List<PresentationSlideElement> elements,
        boolean hidden,
        Instant createdAt,
        Instant updatedAt
) {
}
