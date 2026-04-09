package com.willmear.sprint.api.response;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceSummaryResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
