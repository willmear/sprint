package com.willmear.sprint.api.request;

public record SyncSprintRequest(
        Long boardId,
        Boolean includeComments,
        Boolean includeChangelog
) {
}
