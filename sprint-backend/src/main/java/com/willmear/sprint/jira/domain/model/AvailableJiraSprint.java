package com.willmear.sprint.jira.domain.model;

import java.time.Instant;

public record AvailableJiraSprint(
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
