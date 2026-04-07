package com.willmear.sprint.retrieval.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EmbeddingDocument(
        UUID id,
        UUID workspaceId,
        UUID jiraConnectionId,
        Long externalSprintId,
        String sourceType,
        String sourceId,
        String sourceKey,
        String title,
        String content,
        String chunkText,
        Integer chunkIndex,
        Integer tokenCountEstimate,
        JsonNode metadata,
        List<Double> embedding,
        Instant indexedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
