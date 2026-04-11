package com.willmear.sprint.presentation.domain;

import java.time.Instant;
import java.util.UUID;

public record PresentationSlideElement(
        UUID id,
        UUID slideId,
        Integer elementOrder,
        SlideElementType elementType,
        SlideElementRole role,
        String textContent,
        Double x,
        Double y,
        Double width,
        Double height,
        String fontFamily,
        Integer fontSize,
        boolean bold,
        boolean italic,
        TextAlignment textAlignment,
        Instant createdAt,
        Instant updatedAt
) {
}
