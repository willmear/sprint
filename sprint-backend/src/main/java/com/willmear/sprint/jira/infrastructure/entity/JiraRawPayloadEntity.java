package com.willmear.sprint.jira.infrastructure.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.persistence.converter.JsonNodeConverter;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "jira_raw_payload")
public class JiraRawPayloadEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jira_connection_id", nullable = false)
    private JiraConnectionEntity jiraConnection;

    @Column(name = "payload_type", nullable = false, length = 100)
    private String payloadType;

    @Column(name = "external_reference", length = 255)
    private String externalReference;

    @Column(name = "sync_scope_type", length = 100)
    private String syncScopeType;

    @Column(name = "sync_scope_reference", length = 255)
    private String syncScopeReference;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "payload", columnDefinition = "text", nullable = false)
    private JsonNode payload;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public JiraConnectionEntity getJiraConnection() {
        return jiraConnection;
    }

    public void setJiraConnection(JiraConnectionEntity jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public String getSyncScopeType() {
        return syncScopeType;
    }

    public void setSyncScopeType(String syncScopeType) {
        this.syncScopeType = syncScopeType;
    }

    public String getSyncScopeReference() {
        return syncScopeReference;
    }

    public void setSyncScopeReference(String syncScopeReference) {
        this.syncScopeReference = syncScopeReference;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
