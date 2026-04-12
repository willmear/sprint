package com.willmear.sprint.ai.model;

import java.util.List;

public record PresentationPlanSlideAiResponse(
        String slideIntent,
        String title,
        String subtitle,
        String layoutHint,
        List<PresentationPlanBlockAiResponse> blocks,
        String speakerNotes
) {
}
