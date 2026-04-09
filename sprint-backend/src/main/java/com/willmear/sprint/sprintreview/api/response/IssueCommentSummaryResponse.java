package com.willmear.sprint.sprintreview.api.response;

import java.time.Instant;

public record IssueCommentSummaryResponse(
        String authorDisplayName,
        String body,
        Instant createdAtExternal
) {
}
