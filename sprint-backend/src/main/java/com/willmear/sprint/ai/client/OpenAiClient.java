package com.willmear.sprint.ai.client;

import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;

public interface OpenAiClient {

    AiResponse generate(AiGenerationRequest request);

    EmbeddingResponse embed(EmbeddingRequest request);
}
