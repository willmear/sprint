package com.willmear.sprint.retrieval.domain.port;

import com.willmear.sprint.retrieval.domain.model.EmbeddingDocument;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import java.util.List;
import java.util.UUID;

public interface EmbeddingStorePort {

    int replaceSprintDocuments(UUID workspaceId, Long externalSprintId, List<EmbeddingDocument> documents);

    RetrievalResultSet search(RetrievalQuery query, List<Double> queryEmbedding);

    List<EmbeddingDocumentSummary> listSprintDocuments(UUID workspaceId, Long externalSprintId);

    int deleteSprintDocuments(UUID workspaceId, Long externalSprintId);
}
