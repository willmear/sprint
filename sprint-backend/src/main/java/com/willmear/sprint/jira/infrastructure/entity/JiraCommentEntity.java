package com.willmear.sprint.jira.infrastructure.entity;

import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "jira_comment")
public class JiraCommentEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_issue_id", nullable = false)
    private JiraIssueEntity jiraIssue;

    @Column(name = "external_comment_id", nullable = false, length = 100)
    private String externalCommentId;

    @Column(name = "issue_key", nullable = false, length = 100)
    private String issueKey;

    @Column(name = "author_display_name", length = 255)
    private String authorDisplayName;

    @Column(columnDefinition = "text")
    private String body;

    @Column(name = "created_at_external")
    private Instant createdAtExternal;

    @Column(name = "updated_at_external")
    private Instant updatedAtExternal;

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public JiraIssueEntity getJiraIssue() {
        return jiraIssue;
    }

    public void setJiraIssue(JiraIssueEntity jiraIssue) {
        this.jiraIssue = jiraIssue;
    }

    public String getExternalCommentId() {
        return externalCommentId;
    }

    public void setExternalCommentId(String externalCommentId) {
        this.externalCommentId = externalCommentId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getCreatedAtExternal() {
        return createdAtExternal;
    }

    public void setCreatedAtExternal(Instant createdAtExternal) {
        this.createdAtExternal = createdAtExternal;
    }

    public Instant getUpdatedAtExternal() {
        return updatedAtExternal;
    }

    public void setUpdatedAtExternal(Instant updatedAtExternal) {
        this.updatedAtExternal = updatedAtExternal;
    }
}
