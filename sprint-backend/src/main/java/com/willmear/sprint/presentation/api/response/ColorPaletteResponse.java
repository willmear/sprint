package com.willmear.sprint.presentation.api.response;

public record ColorPaletteResponse(
        String background,
        String surface,
        String textPrimary,
        String textSecondary,
        String accent,
        String accentSecondary,
        String danger,
        String mutedBorder
) {
}
