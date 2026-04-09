package com.willmear.sprint.retrieval.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

public record RetrievalQuery(
        UUID workspaceId,
        String queryText,
        Integer topK,
        Long externalSprintId,
        String sourceType,
        JsonNode metadataFilters,
        boolean includeContent,
        boolean includeScores
) {
}
