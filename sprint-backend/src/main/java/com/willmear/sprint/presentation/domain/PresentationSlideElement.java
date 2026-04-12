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
        Integer zIndex,
        Double rotationDegrees,
        String fillColor,
        String borderColor,
        Integer borderWidth,
        String textColor,
        String fontFamily,
        Integer fontSize,
        boolean bold,
        boolean italic,
        boolean underline,
        TextAlignment textAlignment,
        ShapeType shapeType,
        boolean hidden,
        Instant createdAt,
        Instant updatedAt
) {
}
