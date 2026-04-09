package com.willmear.sprint.retrieval.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record DocumentChunk(
        String sourceType,
        String sourceId,
        String sourceKey,
        String title,
        String content,
        String text,
        Integer chunkIndex,
        Integer tokenCountEstimate,
        JsonNode metadata
) {
}
