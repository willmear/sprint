package com.willmear.sprint.presentation.api.response;

public record TypographyScaleResponse(
        String titleFontFamily,
        String bodyFontFamily,
        Integer titleFontSize,
        Integer subtitleFontSize,
        Integer bodyFontSize,
        Integer smallFontSize
) {
}
