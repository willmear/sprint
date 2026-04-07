package com.willmear.sprint.retrieval.api.response;

import java.time.Instant;

public record IndexingResponse(
        int indexedDocuments,
        int deletedDocuments,
        Instant indexedAt,
        String message
) {
}
