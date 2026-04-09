package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraRawPayloadEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraRawPayloadRepository extends JpaRepository<JiraRawPayloadEntity, UUID> {

    void deleteByWorkspace_IdAndJiraConnection_IdAndSyncScopeTypeAndSyncScopeReference(
            UUID workspaceId,
            UUID jiraConnectionId,
            String syncScopeType,
            String syncScopeReference
    );
}
