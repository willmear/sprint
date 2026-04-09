package com.willmear.sprint.artifact.domain;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public record Artifact(
        UUID id,
        UUID workspaceId,
        ArtifactType artifactType,
        ArtifactStatus status,
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
