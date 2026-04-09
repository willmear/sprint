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
@Table(name = "jira_changelog_event")
public class JiraChangelogEventEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_issue_id", nullable = false)
    private JiraIssueEntity jiraIssue;

    @Column(name = "external_history_id", nullable = false, length = 100)
    private String externalHistoryId;

    @Column(name = "issue_key", nullable = false, length = 100)
    private String issueKey;

    @Column(name = "field_name", nullable = false, length = 255)
    private String fieldName;

    @Column(name = "from_value", columnDefinition = "text")
    private String fromValue;

    @Column(name = "to_value", columnDefinition = "text")
    private String toValue;

    @Column(name = "changed_at")
    private Instant changedAt;

    @Column(name = "author_display_name", length = 255)
    private String authorDisplayName;

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

    public String getExternalHistoryId() {
        return externalHistoryId;
    }

    public void setExternalHistoryId(String externalHistoryId) {
        this.externalHistoryId = externalHistoryId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFromValue() {
        return fromValue;
    }

    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
    }

    public String getToValue() {
        return toValue;
    }

    public void setToValue(String toValue) {
        this.toValue = toValue;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }
}
