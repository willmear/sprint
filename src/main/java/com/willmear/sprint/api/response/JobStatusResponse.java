package com.willmear.sprint.api.response;

import java.time.Instant;
import java.util.UUID;

public record JobStatusResponse(
        UUID jobId,
        String type,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
