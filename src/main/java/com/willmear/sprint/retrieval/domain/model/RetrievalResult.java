package com.willmear.sprint.retrieval.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

public record RetrievalResult(
        UUID documentId,
        String sourceType,
        String sourceId,
        String sourceKey,
        String title,
        String contentSnippet,
        Double score,
        JsonNode metadata
) {
}
