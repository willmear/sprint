package com.willmear.sprint.retrieval.api.request;

import jakarta.validation.constraints.NotBlank;

public record RetrievalSearchRequest(
        @NotBlank String queryText,
        Integer topK,
        Long externalSprintId,
        String sourceType,
        Boolean includeContent
) {
}
