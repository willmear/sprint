package com.willmear.sprint.jobs.domain;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public record Job(
        UUID id,
        UUID workspaceId,
        JobType jobType,
        JobStatus status,
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
