package com.willmear.sprint.api.response;

import java.time.Instant;

public record SprintSummaryResponse(
        Long sprintId,
        String name,
        String state,
        Instant startDate,
        Instant endDate,
        Instant completeDate,
        Instant syncedAt
) {
}
