package com.willmear.sprint.ai.client.dto;

import java.util.List;

public record OpenAiEmbeddingResponse(
        String model,
        List<List<Double>> embeddings,
        OpenAiUsageDto usage
) {
}
