package com.willmear.sprint.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        UUID workspaceId,
        String jobType,
        String status,
        String queueName,
        JsonNode payload,
        Integer attemptCount,
        Integer maxAttempts,
        Instant availableAt,
        Instant lockedAt,
        String lockedBy,
        Instant startedAt,
        Instant completedAt,
        Instant failedAt,
        String errorMessage,
        String errorCode,
        Instant createdAt,
        Instant updatedAt
) {
}
