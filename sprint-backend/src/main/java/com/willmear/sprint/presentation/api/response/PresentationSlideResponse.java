package com.willmear.sprint.presentation.api.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PresentationSlideResponse(
        UUID id,
        UUID deckId,
        Integer slideOrder,
        String slideType,
        String title,
        List<String> bulletPoints,
        String bodyText,
        String speakerNotes,
        String sectionLabel,
        String backgroundColor,
        String backgroundStyleType,
        boolean showGrid,
        String layoutType,
        String templateType,
        List<PresentationSlideElementResponse> elements,
        boolean hidden,
        Instant createdAt,
        Instant updatedAt
) {
}
