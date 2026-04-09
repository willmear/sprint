package com.willmear.sprint.ai.domain.model;

import java.util.Map;

public record AiPrompt(
        String name,
        String version,
        String systemPrompt,
        String userPrompt,
        String responseFormat,
        Map<String, Object> metadata
) {
}
