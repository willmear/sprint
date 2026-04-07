package com.willmear.sprint.retrieval.domain.model;

import java.time.Instant;
import java.util.UUID;

public record EmbeddingDocumentSummary(
        UUID id,
        String sourceType,
        String sourceId,
        String sourceKey,
        String title,
        Integer chunkIndex,
        Instant indexedAt
) {
}
