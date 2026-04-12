package com.willmear.sprint.presentation.theme.domain;

public record PresentationTheme(
        String themeId,
        String displayName,
        ColorPalette colorPalette,
        TypographyScale typography,
        SpacingScale spacing,
        SlideThemeStyle defaults
) {
}
