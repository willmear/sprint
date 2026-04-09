package com.willmear.sprint.jobs.domain;

import java.util.UUID;

public record JobFilter(
        UUID workspaceId,
        JobStatus status,
        JobType jobType
) {
}
