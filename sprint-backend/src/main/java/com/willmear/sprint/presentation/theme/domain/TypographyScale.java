package com.willmear.sprint.presentation.theme.domain;

public record TypographyScale(
        String titleFontFamily,
        String bodyFontFamily,
        int titleFontSize,
        int subtitleFontSize,
        int bodyFontSize,
        int smallFontSize
) {
}
