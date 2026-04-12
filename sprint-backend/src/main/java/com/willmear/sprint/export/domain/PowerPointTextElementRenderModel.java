package com.willmear.sprint.export.domain;

import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.TextAlignment;

public record PowerPointTextElementRenderModel(
        String elementId,
        String textContent,
        double x,
        double y,
        double width,
        double height,
        int zIndex,
        String textColor,
        String fontFamily,
        int fontSize,
        boolean bold,
        boolean italic,
        boolean underline,
        TextAlignment textAlignment,
        SlideElementRole role
) {
}
