package com.willmear.sprint.retrieval.domain.model;

import java.time.Instant;

public record IndexingResult(
        int indexedDocuments,
        int deletedDocuments,
        Instant indexedAt,
        String message
) {
}
