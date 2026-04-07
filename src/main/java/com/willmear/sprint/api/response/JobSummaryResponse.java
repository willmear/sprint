package com.willmear.sprint.api.response;

import java.time.Instant;
import java.util.UUID;

public record JobSummaryResponse(
        UUID id,
        UUID workspaceId,
        String jobType,
        String status,
        Integer attemptCount,
        Integer maxAttempts,
        Instant availableAt,
        Instant createdAt,
        Instant updatedAt
) {
}
