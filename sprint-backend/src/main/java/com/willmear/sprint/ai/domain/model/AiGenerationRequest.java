package com.willmear.sprint.ai.domain.model;

public record AiGenerationRequest(
        String workflowName,
        String promptName,
        String model,
        AiPrompt prompt,
        Double temperature,
        Integer maxOutputTokens,
        boolean structuredOutputExpected,
        String responseSchemaHint
) {
}
