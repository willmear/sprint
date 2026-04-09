package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.infrastructure.entity.JiraChangelogEventEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraChangelogMapper {

    public JiraChangelogEventEntity toEntity(UUID workspaceId, UUID issueEntityId, JiraChangelogEvent event) {
        JiraChangelogEventEntity entity = new JiraChangelogEventEntity();
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        JiraIssueEntity issue = new JiraIssueEntity();
        issue.setId(issueEntityId);
        entity.setWorkspace(workspace);
        entity.setJiraIssue(issue);
        entity.setExternalHistoryId(event.externalHistoryId());
        entity.setIssueKey(event.issueKey());
        entity.setFieldName(event.fieldName());
        entity.setFromValue(event.fromValue());
        entity.setToValue(event.toValue());
        entity.setChangedAt(event.changedAt());
        entity.setAuthorDisplayName(event.authorDisplayName());
        return entity;
    }
}
