package com.willmear.sprint.presentation.repository;

import com.willmear.sprint.presentation.entity.PresentationDeckEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationDeckRepository extends JpaRepository<PresentationDeckEntity, UUID> {

    Optional<PresentationDeckEntity> findByIdAndWorkspaceId(UUID deckId, UUID workspaceId);

    Optional<PresentationDeckEntity> findFirstByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByUpdatedAtDesc(
            UUID workspaceId,
            String referenceType,
            String referenceId
    );
}
