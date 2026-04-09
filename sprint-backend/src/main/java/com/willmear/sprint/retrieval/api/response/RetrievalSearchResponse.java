package com.willmear.sprint.retrieval.api.response;

import java.time.Instant;
import java.util.List;

public record RetrievalSearchResponse(
        List<RetrievalResultResponse> results,
        Integer totalReturned,
        Instant retrievedAt
) {
}
