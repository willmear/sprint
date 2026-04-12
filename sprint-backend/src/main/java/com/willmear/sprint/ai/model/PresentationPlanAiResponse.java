package com.willmear.sprint.ai.model;

import java.util.List;

public record PresentationPlanAiResponse(
        String title,
        String subtitle,
        List<PresentationPlanSlideAiResponse> slides
) {
}
