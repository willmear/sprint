package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraCommentMapper {

    public JiraCommentEntity toEntity(UUID workspaceId, UUID issueEntityId, JiraComment comment) {
        JiraCommentEntity entity = new JiraCommentEntity();
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        JiraIssueEntity issue = new JiraIssueEntity();
        issue.setId(issueEntityId);
        entity.setWorkspace(workspace);
        entity.setJiraIssue(issue);
        entity.setExternalCommentId(comment.externalCommentId());
        entity.setIssueKey(comment.issueKey());
        entity.setAuthorDisplayName(comment.authorDisplayName());
        entity.setBody(comment.body());
        entity.setCreatedAtExternal(comment.createdAtExternal());
        entity.setUpdatedAtExternal(comment.updatedAtExternal());
        return entity;
    }
}
