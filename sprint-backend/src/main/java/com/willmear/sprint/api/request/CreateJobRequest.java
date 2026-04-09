package com.willmear.sprint.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jobs.domain.JobType;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CreateJobRequest(
        UUID workspaceId,
        @NotNull JobType jobType,
        JsonNode payload,
        Integer maxAttempts,
        Instant availableAt
) {
}
