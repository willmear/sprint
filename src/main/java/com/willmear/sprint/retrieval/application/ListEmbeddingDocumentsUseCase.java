package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.retrieval.domain.model.EmbeddingDocumentSummary;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListEmbeddingDocumentsUseCase {

    private final EmbeddingStorePort embeddingStorePort;

    public ListEmbeddingDocumentsUseCase(EmbeddingStorePort embeddingStorePort) {
        this.embeddingStorePort = embeddingStorePort;
    }

    public List<EmbeddingDocumentSummary> list(UUID workspaceId, Long externalSprintId) {
        return embeddingStorePort.listSprintDocuments(workspaceId, externalSprintId);
    }
}
