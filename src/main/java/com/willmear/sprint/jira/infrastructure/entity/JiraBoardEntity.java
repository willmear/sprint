package com.willmear.sprint.jira.infrastructure.entity;

import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jira_board")
public class JiraBoardEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(name = "external_board_id", nullable = false)
    private Long externalBoardId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "board_type", length = 100)
    private String boardType;

    @Column(name = "project_key", length = 100)
    private String projectKey;

    public WorkspaceEntity getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceEntity workspace) { this.workspace = workspace; }
    public Long getExternalBoardId() { return externalBoardId; }
    public void setExternalBoardId(Long externalBoardId) { this.externalBoardId = externalBoardId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBoardType() { return boardType; }
    public void setBoardType(String boardType) { this.boardType = boardType; }
    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }
}
