package com.willmear.sprint.ai.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AiRun(
        UUID id,
        String workflowName,
        String promptName,
        String promptVersion,
        String model,
        Instant startedAt,
        Instant completedAt,
        TokenUsage tokenUsage,
        boolean success,
        String errorMessage
) {
}
