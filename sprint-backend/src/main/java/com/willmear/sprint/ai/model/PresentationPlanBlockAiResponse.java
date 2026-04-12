package com.willmear.sprint.ai.model;

import java.util.List;

public record PresentationPlanBlockAiResponse(
        String blockType,
        String text,
        List<String> items,
        String visualPriority
) {
}
