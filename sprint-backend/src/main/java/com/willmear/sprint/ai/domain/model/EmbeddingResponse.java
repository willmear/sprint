package com.willmear.sprint.ai.domain.model;

import java.util.List;

public record EmbeddingResponse(
        List<List<Double>> embeddings,
        String model,
        TokenUsage tokenUsage
) {
}
