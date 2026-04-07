package com.willmear.sprint.retrieval.api;

import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import java.util.List;
import java.util.UUID;

public interface RetrievalService {

    IndexingResult indexSprintDocuments(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary, boolean forceReindex);

    RetrievalResultSet search(RetrievalQuery query);

    List<EmbeddingDocumentSummary> listSprintDocuments(UUID workspaceId, Long externalSprintId);

    int deleteSprintDocuments(UUID workspaceId, Long externalSprintId);
}
