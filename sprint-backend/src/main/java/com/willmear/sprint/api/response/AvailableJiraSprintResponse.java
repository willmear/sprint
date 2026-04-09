package com.willmear.sprint.api.response;

import java.time.Instant;

public record AvailableJiraSprintResponse(
        Long sprintId,
        String sprintName,
        String state,
        Long boardId,
        String boardName,
        Instant startDate,
        Instant endDate,
        Instant completeDate
) {
}
