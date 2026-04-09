package com.willmear.sprint.retrieval.api.response;

import java.time.Instant;
import java.util.UUID;

public record EmbeddingDocumentSummaryResponse(
        UUID id,
        String sourceType,
        String sourceId,
        String sourceKey,
        String title,
        Integer chunkIndex,
        Instant indexedAt
) {
}
