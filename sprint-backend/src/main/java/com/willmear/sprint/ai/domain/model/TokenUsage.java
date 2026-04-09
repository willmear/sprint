package com.willmear.sprint.ai.domain.model;

public record TokenUsage(
        Integer inputTokens,
        Integer outputTokens,
        Integer totalTokens
) {
}
