package com.willmear.sprint.retrieval.infrastructure.vector;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.common.exception.EmbeddingGenerationException;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.port.EmbeddingGeneratorPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AiEmbeddingGeneratorAdapter implements EmbeddingGeneratorPort {

    private final AiGenerationService aiGenerationService;
    private final OpenAiProperties openAiProperties;
    private final RetrievalProperties retrievalProperties;

    public AiEmbeddingGeneratorAdapter(
            AiGenerationService aiGenerationService,
            OpenAiProperties openAiProperties,
            RetrievalProperties retrievalProperties
    ) {
        this.aiGenerationService = aiGenerationService;
        this.openAiProperties = openAiProperties;
        this.retrievalProperties = retrievalProperties;
    }

    @Override
    public List<List<Double>> generateEmbeddings(List<String> contents) {
        try {
            return aiGenerationService.embed(new EmbeddingRequest(openAiProperties.embeddingModel(), contents)).embeddings().stream()
                    .map(this::normalize)
                    .toList();
        } catch (RuntimeException exception) {
            throw new EmbeddingGenerationException("Failed to generate embeddings.", exception);
        }
    }

    @Override
    public List<Double> generateEmbedding(String content) {
        List<List<Double>> embeddings = generateEmbeddings(List.of(content));
        return embeddings.isEmpty() ? List.of() : embeddings.getFirst();
    }

    private List<Double> normalize(List<Double> embedding) {
        int dimension = retrievalProperties.embeddingDimension();
        List<Double> normalized = new ArrayList<>(dimension);
        if (embedding != null) {
            normalized.addAll(embedding.stream().limit(dimension).toList());
        }
        while (normalized.size() < dimension) {
            normalized.add(0.0d);
        }
        return normalized;
    }
}
