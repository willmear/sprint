package com.willmear.sprint.ai.domain.model;

public record AiGenerationResult<T>(
        T parsedResult,
        AiResponse rawResponse,
        AiRun aiRun
) {
}
