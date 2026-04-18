package com.willmear.sprint.workspace.repository;

import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {

    List<WorkspaceEntity> findByOwner_IdOrderByCreatedAtAsc(UUID ownerUserId);

    Optional<WorkspaceEntity> findByIdAndOwner_Id(UUID workspaceId, UUID ownerUserId);
}
