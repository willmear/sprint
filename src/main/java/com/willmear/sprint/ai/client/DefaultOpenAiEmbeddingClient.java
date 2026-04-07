package com.willmear.sprint.ai.client;

import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import org.springframework.stereotype.Component;

@Component
public class DefaultOpenAiEmbeddingClient implements OpenAiEmbeddingClient {

    private final OpenAiClient openAiClient;

    public DefaultOpenAiEmbeddingClient(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) {
        return openAiClient.embed(request);
    }
}
