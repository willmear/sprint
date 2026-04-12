package com.willmear.sprint.presentation.api.response;

public record PresentationThemeSummaryResponse(
        String themeId,
        String displayName,
        ColorPaletteResponse colorPalette,
        TypographyScaleResponse typography
) {
}
