package com.willmear.sprint.api.response;

import java.time.Instant;

public record SprintResponse(
        Long sprintId,
        String name,
        String state,
        String goal,
        Long boardId,
        int issueCount,
        Instant startDate,
        Instant endDate,
        Instant completeDate,
        Instant syncedAt
) {
}
