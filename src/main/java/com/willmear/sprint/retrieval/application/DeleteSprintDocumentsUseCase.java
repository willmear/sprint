package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeleteSprintDocumentsUseCase {

    private final EmbeddingStorePort embeddingStorePort;

    public DeleteSprintDocumentsUseCase(EmbeddingStorePort embeddingStorePort) {
        this.embeddingStorePort = embeddingStorePort;
    }

    public int delete(UUID workspaceId, Long externalSprintId) {
        return embeddingStorePort.deleteSprintDocuments(workspaceId, externalSprintId);
    }
}
