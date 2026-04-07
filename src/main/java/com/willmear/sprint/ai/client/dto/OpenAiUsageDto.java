package com.willmear.sprint.ai.client.dto;

public record OpenAiUsageDto(
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}
