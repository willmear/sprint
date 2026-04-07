package com.willmear.sprint.ai.domain.model;

import java.time.Instant;

public record AiResponse(
        String content,
        String model,
        TokenUsage tokenUsage,
        Instant createdAt,
        String finishReason,
        boolean success,
        String refusalReason
) {
}
