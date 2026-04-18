package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraChangelogEventEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraChangelogEventRepository extends JpaRepository<JiraChangelogEventEntity, UUID> {

    void deleteByJiraIssue_Id(UUID jiraIssueId);

    void deleteByJiraIssue_JiraConnection_Id(UUID jiraConnectionId);

    List<JiraChangelogEventEntity> findByJiraIssue_IdOrderByChangedAtAsc(UUID jiraIssueId);
}
