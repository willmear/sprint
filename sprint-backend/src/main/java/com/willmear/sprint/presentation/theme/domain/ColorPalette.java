package com.willmear.sprint.presentation.theme.domain;

public record ColorPalette(
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
