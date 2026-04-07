package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraCommentRepository extends JpaRepository<JiraCommentEntity, UUID> {

    void deleteByJiraIssue_Id(UUID jiraIssueId);

    List<JiraCommentEntity> findByJiraIssue_IdOrderByCreatedAtExternalAsc(UUID jiraIssueId);
}
