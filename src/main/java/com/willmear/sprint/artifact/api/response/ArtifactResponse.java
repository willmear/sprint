package com.willmear.sprint.artifact.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public record ArtifactResponse(
        UUID id,
        UUID workspaceId,
        String artifactType,
        String status,
        String referenceType,
        String referenceId,
        JsonNode structuredContent,
        String renderedMarkdown,
        String title,
        String summary,
        String generatorType,
        String generatorVersion,
        Instant generatedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
