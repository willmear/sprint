package com.willmear.sprint.export.renderer;

import java.awt.Color;

public record PowerPointSlideStyle(
        Color defaultBackgroundColor,
        Color titleColor,
        Color subtitleColor,
        Color bodyColor,
        Color accentColor,
        Color accentSecondaryColor,
        boolean fullBleedHeaderAccent,
        boolean leftAccentRail,
        boolean fullBleedBottomAccent,
        boolean emphasizeCallouts,
        double topInset,
        double bodyTopInset,
        double bulletLeftMargin,
        double bulletIndent,
        double paragraphSpaceAfter
) {
}
