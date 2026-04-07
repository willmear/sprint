package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.retrieval.api.RetrievalService;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RetrievalApplicationService implements RetrievalService {

    private final IndexSprintDocumentsUseCase indexSprintDocumentsUseCase;
    private final SearchSprintContextUseCase searchSprintContextUseCase;
    private final ListEmbeddingDocumentsUseCase listEmbeddingDocumentsUseCase;
    private final DeleteSprintDocumentsUseCase deleteSprintDocumentsUseCase;

    public RetrievalApplicationService(
            IndexSprintDocumentsUseCase indexSprintDocumentsUseCase,
            SearchSprintContextUseCase searchSprintContextUseCase,
            ListEmbeddingDocumentsUseCase listEmbeddingDocumentsUseCase,
            DeleteSprintDocumentsUseCase deleteSprintDocumentsUseCase
    ) {
        this.indexSprintDocumentsUseCase = indexSprintDocumentsUseCase;
        this.searchSprintContextUseCase = searchSprintContextUseCase;
        this.listEmbeddingDocumentsUseCase = listEmbeddingDocumentsUseCase;
        this.deleteSprintDocumentsUseCase = deleteSprintDocumentsUseCase;
    }

    @Override
    public IndexingResult indexSprintDocuments(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary, boolean forceReindex) {
        return indexSprintDocumentsUseCase.index(workspaceId, externalSprintId, includeComments, includeSprintSummary, forceReindex);
    }

    @Override
    public RetrievalResultSet search(RetrievalQuery query) {
        return searchSprintContextUseCase.search(query);
    }

    @Override
    public List<EmbeddingDocumentSummary> listSprintDocuments(UUID workspaceId, Long externalSprintId) {
        return listEmbeddingDocumentsUseCase.list(workspaceId, externalSprintId);
    }

    @Override
    public int deleteSprintDocuments(UUID workspaceId, Long externalSprintId) {
        return deleteSprintDocumentsUseCase.delete(workspaceId, externalSprintId);
    }
}
