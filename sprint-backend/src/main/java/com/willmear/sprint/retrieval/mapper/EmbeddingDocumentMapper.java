package com.willmear.sprint.retrieval.mapper;

import com.willmear.sprint.retrieval.domain.model.EmbeddingDocument;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.infrastructure.entity.EmbeddingDocumentEntity;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingDocumentMapper {

    public EmbeddingDocumentEntity toEntity(EmbeddingDocument document) {
        EmbeddingDocumentEntity entity = new EmbeddingDocumentEntity();
        entity.setId(document.id());
        entity.setWorkspaceId(document.workspaceId());
        entity.setJiraConnectionId(document.jiraConnectionId());
        entity.setExternalSprintId(document.externalSprintId());
        entity.setSourceType(document.sourceType());
        entity.setSourceId(document.sourceId());
        entity.setSourceKey(document.sourceKey());
        entity.setTitle(document.title());
        entity.setContent(document.content());
        entity.setChunkText(document.chunkText());
        entity.setChunkIndex(document.chunkIndex());
        entity.setTokenCountEstimate(document.tokenCountEstimate());
        entity.setMetadata(document.metadata());
        entity.setEmbedding(toVector(document.embedding()));
        entity.setIndexedAt(document.indexedAt());
        entity.setCreatedAt(document.createdAt());
        entity.setUpdatedAt(document.updatedAt());
        return entity;
    }

    public EmbeddingDocumentSummary toSummary(EmbeddingDocumentEntity entity) {
        return new EmbeddingDocumentSummary(
                entity.getId(),
                entity.getSourceType(),
                entity.getSourceId(),
                entity.getSourceKey(),
                entity.getTitle(),
                entity.getChunkIndex(),
                entity.getIndexedAt()
        );
    }

    public String toVectorLiteral(List<Double> embedding) {
        return toVectorLiteralInternal(embedding);
    }

    public List<Double> parseVector(String value) {
        if (value == null || value.isBlank() || "[]".equals(value)) {
            return List.of();
        }
        String normalized = value.replace("[", "").replace("]", "");
        if (normalized.isBlank()) {
            return List.of();
        }
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(Double::parseDouble)
                .toList();
    }

    public double[] toVector(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return new double[0];
        }
        double[] vector = new double[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = embedding.get(i);
        }
        return vector;
    }

    private String toVectorLiteralInternal(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return "[]";
        }
        return "[" + embedding.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) + "]";
    }
}
