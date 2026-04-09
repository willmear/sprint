package com.willmear.sprint.retrieval.mapper;

import com.willmear.sprint.retrieval.api.response.EmbeddingDocumentSummaryResponse;
import com.willmear.sprint.retrieval.api.response.IndexingResponse;
import com.willmear.sprint.retrieval.api.response.RetrievalResultResponse;
import com.willmear.sprint.retrieval.api.response.RetrievalSearchResponse;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import org.springframework.stereotype.Component;

@Component
public class RetrievalResultMapper {

    public IndexingResponse toResponse(IndexingResult result) {
        return new IndexingResponse(result.indexedDocuments(), result.deletedDocuments(), result.indexedAt(), result.message());
    }

    public RetrievalSearchResponse toResponse(RetrievalResultSet resultSet) {
        return new RetrievalSearchResponse(
                resultSet.results().stream().map(this::toResponse).toList(),
                resultSet.totalReturned(),
                resultSet.retrievedAt()
        );
    }

    public RetrievalResultResponse toResponse(RetrievalResult result) {
        return new RetrievalResultResponse(
                result.documentId(),
                result.sourceType(),
                result.sourceId(),
                result.sourceKey(),
                result.title(),
                result.contentSnippet(),
                result.score(),
                result.metadata()
        );
    }

    public EmbeddingDocumentSummaryResponse toResponse(EmbeddingDocumentSummary summary) {
        return new EmbeddingDocumentSummaryResponse(
                summary.id(),
                summary.sourceType(),
                summary.sourceId(),
                summary.sourceKey(),
                summary.title(),
                summary.chunkIndex(),
                summary.indexedAt()
        );
    }
}
