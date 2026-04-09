package com.willmear.sprint.ai.api;

import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import java.util.function.Function;

public interface AiGenerationService {

    AiResponse generateRaw(AiGenerationRequest request);

    <T> AiGenerationResult<T> generate(AiGenerationRequest request, Function<AiResponse, T> parser);

    EmbeddingResponse embed(EmbeddingRequest request);
}
