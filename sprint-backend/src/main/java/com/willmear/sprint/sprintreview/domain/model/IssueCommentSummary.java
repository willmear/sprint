package com.willmear.sprint.sprintreview.domain.model;

import java.time.Instant;

public record IssueCommentSummary(
        String authorDisplayName,
        String body,
        Instant createdAtExternal
) {
}
