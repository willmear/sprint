package com.willmear.sprint.workspace.repository;

import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {
}

