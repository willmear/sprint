package com.willmear.sprint.presentationplan.domain;

import java.util.List;

public record SlideBlock(
        SlideBlockType blockType,
        String heading,
        List<String> items,
        String body,
        VisualPriority visualPriority
) {
}
