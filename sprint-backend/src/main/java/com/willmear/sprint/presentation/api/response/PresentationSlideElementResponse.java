package com.willmear.sprint.presentation.api.response;

import java.time.Instant;
import java.util.UUID;

public record PresentationSlideElementResponse(
        UUID id,
        UUID slideId,
        Integer elementOrder,
        String elementType,
        String role,
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
        String textAlignment,
        String shapeType,
        boolean hidden,
        Instant createdAt,
        Instant updatedAt
) {
}
