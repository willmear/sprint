package com.willmear.sprint.artifact.api.response;

import java.time.Instant;
import java.util.UUID;

public record ArtifactSummaryResponse(
        UUID id,
        UUID workspaceId,
        String artifactType,
        String status,
        String referenceType,
        String referenceId,
        String title,
        String summary,
        String generatorType,
        Instant generatedAt,
        Instant createdAt
) {
}
