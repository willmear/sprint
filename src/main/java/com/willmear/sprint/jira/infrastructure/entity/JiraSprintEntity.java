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
@Table(name = "jira_sprint")
public class JiraSprintEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_connection_id", nullable = false)
    private JiraConnectionEntity jiraConnection;

    @Column(name = "external_sprint_id", nullable = false)
    private Long externalSprintId;

    @Column(name = "external_board_id")
    private Long externalBoardId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String goal;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "complete_date")
    private Instant completeDate;

    @Column(name = "synced_at", nullable = false)
    private Instant syncedAt;

    public WorkspaceEntity getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceEntity workspace) { this.workspace = workspace; }
    public JiraConnectionEntity getJiraConnection() { return jiraConnection; }
    public void setJiraConnection(JiraConnectionEntity jiraConnection) { this.jiraConnection = jiraConnection; }
    public Long getExternalSprintId() { return externalSprintId; }
    public void setExternalSprintId(Long externalSprintId) { this.externalSprintId = externalSprintId; }
    public Long getExternalBoardId() { return externalBoardId; }
    public void setExternalBoardId(Long externalBoardId) { this.externalBoardId = externalBoardId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public Instant getCompleteDate() { return completeDate; }
    public void setCompleteDate(Instant completeDate) { this.completeDate = completeDate; }
    public Instant getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Instant syncedAt) { this.syncedAt = syncedAt; }
}
