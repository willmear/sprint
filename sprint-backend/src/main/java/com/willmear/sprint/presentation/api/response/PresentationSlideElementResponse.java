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
        String fontFamily,
        Integer fontSize,
        boolean bold,
        boolean italic,
        String textAlignment,
        Instant createdAt,
        Instant updatedAt
) {
}
