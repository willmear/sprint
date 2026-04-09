package com.willmear.sprint.retrieval.infrastructure.repository;

import com.willmear.sprint.retrieval.infrastructure.entity.EmbeddingDocumentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingDocumentRepository extends JpaRepository<EmbeddingDocumentEntity, UUID> {

    int deleteByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId);

    List<EmbeddingDocumentEntity> findByWorkspaceIdAndExternalSprintIdOrderByIndexedAtDescChunkIndexAsc(UUID workspaceId, Long externalSprintId);
}
