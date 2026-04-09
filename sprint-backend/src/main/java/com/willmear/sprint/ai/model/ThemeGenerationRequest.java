package com.willmear.sprint.ai.model;

import com.willmear.sprint.sprintreview.domain.model.SprintContext;

public record ThemeGenerationRequest(
        SprintContext context,
        String model,
        Double temperature,
        Integer maxOutputTokens,
        String audience,
        String tone
) {
}
