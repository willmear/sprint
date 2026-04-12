package com.willmear.sprint.export.domain;

import com.willmear.sprint.presentation.domain.ShapeType;

public record PowerPointShapeElementRenderModel(
        String elementId,
        ShapeType shapeType,
        double x,
        double y,
        double width,
        double height,
        int zIndex,
        double rotationDegrees,
        String fillColor,
        String borderColor,
        int borderWidth
) {
}
