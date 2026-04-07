package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraConnectionRepository extends JpaRepository<JiraConnectionEntity, UUID> {

    List<JiraConnectionEntity> findByWorkspace_IdOrderByCreatedAtDesc(UUID workspaceId);

    Optional<JiraConnectionEntity> findByIdAndWorkspace_Id(UUID connectionId, UUID workspaceId);
}
