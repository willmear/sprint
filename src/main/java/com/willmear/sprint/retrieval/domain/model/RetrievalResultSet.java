package com.willmear.sprint.retrieval.domain.model;

import java.time.Instant;
import java.util.List;

public record RetrievalResultSet(
        List<RetrievalResult> results,
        Integer totalReturned,
        Instant retrievedAt
) {
}
