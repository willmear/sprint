package com.willmear.sprint.ai.domain.model;

import java.util.List;

public record EmbeddingRequest(
        String model,
        List<String> input
) {
}
