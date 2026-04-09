package com.willmear.sprint.ai.model;

import com.willmear.sprint.sprintreview.domain.model.SprintContext;

public record BlockerGenerationRequest(
        SprintContext context,
        String model,
        Double temperature,
        Integer maxOutputTokens,
        String audience,
        String tone
) {
}
