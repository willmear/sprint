package com.willmear.sprint.retrieval.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.model.DocumentChunk;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ChunkingService {

    private final RetrievalProperties retrievalProperties;

    public ChunkingService(RetrievalProperties retrievalProperties) {
        this.retrievalProperties = retrievalProperties;
    }

    public List<DocumentChunk> chunk(DocumentChunk seed) {
        String text = seed.text() == null ? "" : seed.text().trim();
        if (text.isBlank()) {
            return List.of();
        }

        int chunkSize = Math.max(200, retrievalProperties.chunkSizeChars());
        int overlap = Math.max(0, Math.min(retrievalProperties.chunkOverlapChars(), chunkSize / 2));
        List<DocumentChunk> chunks = new ArrayList<>();
        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {
            int end = Math.min(text.length(), start + chunkSize);
            String chunkText = text.substring(start, end).trim();
            if (!chunkText.isBlank()) {
                chunks.add(new DocumentChunk(
                        seed.sourceType(),
                        seed.sourceId(),
                        seed.sourceKey(),
                        seed.title(),
                        seed.content(),
                        chunkText,
                        chunkIndex,
                        estimateTokens(chunkText),
                        seed.metadata()
                ));
                chunkIndex++;
            }
            if (end >= text.length()) {
                break;
            }
            start = Math.max(end - overlap, start + 1);
        }

        return chunks;
    }

    public DocumentChunk createSeed(
            String sourceType,
            String sourceId,
            String sourceKey,
            String title,
            String content,
            JsonNode metadata
    ) {
        return new DocumentChunk(sourceType, sourceId, sourceKey, title, content, content, 0, estimateTokens(content), metadata);
    }

    private int estimateTokens(String text) {
        // TODO: Replace with model-aware token estimation when tokenizer support is added.
        return Math.max(1, text.length() / 4);
    }
}
