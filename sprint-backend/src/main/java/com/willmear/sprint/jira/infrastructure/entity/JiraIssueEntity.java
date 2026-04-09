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
@Table(name = "jira_issue")
public class JiraIssueEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_connection_id", nullable = false)
    private JiraConnectionEntity jiraConnection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_sprint_id", nullable = false)
    private JiraSprintEntity jiraSprint;

    @Column(name = "external_sprint_id", nullable = false)
    private Long externalSprintId;

    @Column(name = "issue_key", nullable = false, length = 100)
    private String issueKey;

    @Column(name = "external_issue_id", nullable = false, length = 100)
    private String externalIssueId;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "issue_type", length = 100)
    private String issueType;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String priority;

    @Column(name = "assignee_display_name", length = 255)
    private String assigneeDisplayName;

    @Column(name = "reporter_display_name", length = 255)
    private String reporterDisplayName;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "created_at_external")
    private Instant createdAtExternal;

    @Column(name = "updated_at_external")
    private Instant updatedAtExternal;

    public WorkspaceEntity getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceEntity workspace) { this.workspace = workspace; }
    public JiraConnectionEntity getJiraConnection() { return jiraConnection; }
    public void setJiraConnection(JiraConnectionEntity jiraConnection) { this.jiraConnection = jiraConnection; }
    public JiraSprintEntity getJiraSprint() { return jiraSprint; }
    public void setJiraSprint(JiraSprintEntity jiraSprint) { this.jiraSprint = jiraSprint; }
    public Long getExternalSprintId() { return externalSprintId; }
    public void setExternalSprintId(Long externalSprintId) { this.externalSprintId = externalSprintId; }
    public String getIssueKey() { return issueKey; }
    public void setIssueKey(String issueKey) { this.issueKey = issueKey; }
    public String getExternalIssueId() { return externalIssueId; }
    public void setExternalIssueId(String externalIssueId) { this.externalIssueId = externalIssueId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getAssigneeDisplayName() { return assigneeDisplayName; }
    public void setAssigneeDisplayName(String assigneeDisplayName) { this.assigneeDisplayName = assigneeDisplayName; }
    public String getReporterDisplayName() { return reporterDisplayName; }
    public void setReporterDisplayName(String reporterDisplayName) { this.reporterDisplayName = reporterDisplayName; }
    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
    public Instant getCreatedAtExternal() { return createdAtExternal; }
    public void setCreatedAtExternal(Instant createdAtExternal) { this.createdAtExternal = createdAtExternal; }
    public Instant getUpdatedAtExternal() { return updatedAtExternal; }
    public void setUpdatedAtExternal(Instant updatedAtExternal) { this.updatedAtExternal = updatedAtExternal; }
}
