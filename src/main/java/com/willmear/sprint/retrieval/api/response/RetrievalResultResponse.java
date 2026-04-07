package com.willmear.sprint.retrieval.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

public record RetrievalResultResponse(
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
