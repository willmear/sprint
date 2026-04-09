package com.willmear.sprint.sprintreview.api.response;

import java.time.Instant;
import java.util.UUID;

public record GenerateSprintReviewJobResponse(
        UUID jobId,
        String status,
        Instant availableAt,
        Instant createdAt
) {
}
