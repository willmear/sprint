package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.retrieval.api.RetrievalService;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import com.willmear.sprint.workspace.application.WorkspaceAuthorizationService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RetrievalApplicationService implements RetrievalService {

    private final IndexSprintDocumentsUseCase indexSprintDocumentsUseCase;
    private final SearchSprintContextUseCase searchSprintContextUseCase;
    private final ListEmbeddingDocumentsUseCase listEmbeddingDocumentsUseCase;
    private final DeleteSprintDocumentsUseCase deleteSprintDocumentsUseCase;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;

    public RetrievalApplicationService(
            IndexSprintDocumentsUseCase indexSprintDocumentsUseCase,
            SearchSprintContextUseCase searchSprintContextUseCase,
            ListEmbeddingDocumentsUseCase listEmbeddingDocumentsUseCase,
            DeleteSprintDocumentsUseCase deleteSprintDocumentsUseCase,
            WorkspaceAuthorizationService workspaceAuthorizationService
    ) {
        this.indexSprintDocumentsUseCase = indexSprintDocumentsUseCase;
        this.searchSprintContextUseCase = searchSprintContextUseCase;
        this.listEmbeddingDocumentsUseCase = listEmbeddingDocumentsUseCase;
        this.deleteSprintDocumentsUseCase = deleteSprintDocumentsUseCase;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    @Override
    public IndexingResult indexSprintDocuments(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary, boolean forceReindex) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return indexSprintDocumentsUseCase.index(workspaceId, externalSprintId, includeComments, includeSprintSummary, forceReindex);
    }

    @Override
    public RetrievalResultSet search(RetrievalQuery query) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(query.workspaceId());
        return searchSprintContextUseCase.search(query);
    }

    @Override
    public List<EmbeddingDocumentSummary> listSprintDocuments(UUID workspaceId, Long externalSprintId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return listEmbeddingDocumentsUseCase.list(workspaceId, externalSprintId);
    }

    @Override
    public int deleteSprintDocuments(UUID workspaceId, Long externalSprintId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return deleteSprintDocumentsUseCase.delete(workspaceId, externalSprintId);
    }
}
