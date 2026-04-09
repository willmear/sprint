package com.willmear.sprint.ai.client;

import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;

public interface OpenAiEmbeddingClient {

    EmbeddingResponse embed(EmbeddingRequest request);
}
