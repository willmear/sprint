package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraIssueRepository extends JpaRepository<JiraIssueEntity, UUID> {

    void deleteByJiraSprint_Id(UUID jiraSprintId);

    void deleteByJiraConnection_Id(UUID jiraConnectionId);

    boolean existsByJiraConnection_Id(UUID jiraConnectionId);

    List<JiraIssueEntity> findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(UUID workspaceId, Long externalSprintId);

    Optional<JiraIssueEntity> findByWorkspace_IdAndExternalSprintIdAndIssueKey(UUID workspaceId, Long externalSprintId, String issueKey);
}
