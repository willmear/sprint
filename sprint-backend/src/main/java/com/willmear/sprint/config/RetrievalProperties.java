package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.retrieval")
public record RetrievalProperties(
        boolean enabled,
        boolean indexComments,
        boolean indexSprintSummary,
        int defaultTopK,
        int chunkSizeChars,
        int chunkOverlapChars,
        int embeddingDimension,
        boolean enrichSprintReviewContext
) {
}
