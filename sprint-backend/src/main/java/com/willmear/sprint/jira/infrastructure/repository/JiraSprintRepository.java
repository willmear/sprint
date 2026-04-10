package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraSprintRepository extends JpaRepository<JiraSprintEntity, UUID> {

    boolean existsByJiraConnection_Id(UUID jiraConnectionId);

    Optional<JiraSprintEntity> findByWorkspace_IdAndExternalSprintId(UUID workspaceId, Long externalSprintId);

    List<JiraSprintEntity> findByWorkspace_IdOrderBySyncedAtDesc(UUID workspaceId);
}
