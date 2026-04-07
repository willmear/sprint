package com.willmear.sprint.retrieval.infrastructure.vector;

import com.willmear.sprint.retrieval.domain.model.EmbeddingDocument;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import com.willmear.sprint.retrieval.infrastructure.repository.EmbeddingDocumentRepository;
import com.willmear.sprint.retrieval.mapper.EmbeddingDocumentMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PgVectorStoreAdapter implements EmbeddingStorePort {

    private final EmbeddingDocumentRepository embeddingDocumentRepository;
    private final PgVectorSearchRepository pgVectorSearchRepository;
    private final EmbeddingDocumentMapper embeddingDocumentMapper;

    public PgVectorStoreAdapter(
            EmbeddingDocumentRepository embeddingDocumentRepository,
            PgVectorSearchRepository pgVectorSearchRepository,
            EmbeddingDocumentMapper embeddingDocumentMapper
    ) {
        this.embeddingDocumentRepository = embeddingDocumentRepository;
        this.pgVectorSearchRepository = pgVectorSearchRepository;
        this.embeddingDocumentMapper = embeddingDocumentMapper;
    }

    @Override
    @Transactional
    public int replaceSprintDocuments(UUID workspaceId, Long externalSprintId, List<EmbeddingDocument> documents) {
        int deleted = embeddingDocumentRepository.deleteByWorkspaceIdAndExternalSprintId(workspaceId, externalSprintId);
        pgVectorSearchRepository.batchInsert(documents);
        return deleted;
    }

    @Override
    public RetrievalResultSet search(RetrievalQuery query, List<Double> queryEmbedding) {
        List<com.willmear.sprint.retrieval.domain.model.RetrievalResult> results = pgVectorSearchRepository.search(query, queryEmbedding);
        return new RetrievalResultSet(results, results.size(), Instant.now());
    }

    @Override
    public List<EmbeddingDocumentSummary> listSprintDocuments(UUID workspaceId, Long externalSprintId) {
        return embeddingDocumentRepository.findByWorkspaceIdAndExternalSprintIdOrderByIndexedAtDescChunkIndexAsc(workspaceId, externalSprintId)
                .stream()
                .map(embeddingDocumentMapper::toSummary)
                .toList();
    }

    @Override
    public int deleteSprintDocuments(UUID workspaceId, Long externalSprintId) {
        return embeddingDocumentRepository.deleteByWorkspaceIdAndExternalSprintId(workspaceId, externalSprintId);
    }
}
