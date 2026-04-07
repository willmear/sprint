package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraBoardEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraBoardRepository extends JpaRepository<JiraBoardEntity, UUID> {

    Optional<JiraBoardEntity> findByWorkspace_IdAndExternalBoardId(UUID workspaceId, Long externalBoardId);
}
