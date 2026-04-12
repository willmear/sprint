package com.willmear.sprint.presentation.template;

import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.TextAlignment;

public record SlideSlot(
        String key,
        SlideElementRole role,
        double x,
        double y,
        double width,
        double height,
        int preferredFontSize,
        int minFontSize,
        boolean bold,
        boolean italic,
        TextAlignment alignment,
        int maxItems
) {
}
