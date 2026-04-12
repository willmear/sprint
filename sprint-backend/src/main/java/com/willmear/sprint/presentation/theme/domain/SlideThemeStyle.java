package com.willmear.sprint.presentation.theme.domain;

public record SlideThemeStyle(
        String slideBackground,
        String titleColor,
        String subtitleColor,
        String bodyColor,
        String sectionLabelColor,
        ThemeAccentStyle accentStyle
) {
}
