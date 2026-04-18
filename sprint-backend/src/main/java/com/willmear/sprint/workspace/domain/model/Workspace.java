package com.willmear.sprint.workspace.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Workspace(
        UUID id,
        UUID ownerUserId,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
