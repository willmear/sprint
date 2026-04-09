package com.willmear.sprint.api.response;

import java.time.Instant;
import java.util.UUID;

public record CreateJobResponse(
        UUID id,
        String status,
        Instant availableAt,
        Instant createdAt
) {
}
