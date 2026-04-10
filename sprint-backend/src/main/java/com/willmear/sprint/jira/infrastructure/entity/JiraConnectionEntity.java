package com.willmear.sprint.jira.infrastructure.entity;

import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "jira_connection")
public class JiraConnectionEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(name = "base_url", nullable = false, length = 512)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 50)
    private JiraAuthType authType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private JiraConnectionStatus status;

    @Column(name = "client_email_or_username", length = 255)
    private String clientEmailOrUsername;

    @Column(name = "encrypted_access_token", columnDefinition = "text")
    private String encryptedAccessToken;

    @Column(name = "encrypted_refresh_token", columnDefinition = "text")
    private String encryptedRefreshToken;

    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;

    @Column(name = "last_tested_at")
    private Instant lastTestedAt;

    @Column(name = "external_account_id", length = 255)
    private String externalAccountId;

    @Column(name = "external_account_display_name", length = 255)
    private String externalAccountDisplayName;

    @Column(name = "external_account_avatar_url", length = 1024)
    private String externalAccountAvatarUrl;

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public JiraAuthType getAuthType() {
        return authType;
    }

    public void setAuthType(JiraAuthType authType) {
        this.authType = authType;
    }

    public JiraConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(JiraConnectionStatus status) {
        this.status = status;
    }

    public String getClientEmailOrUsername() {
        return clientEmailOrUsername;
    }

    public void setClientEmailOrUsername(String clientEmailOrUsername) {
        this.clientEmailOrUsername = clientEmailOrUsername;
    }

    public String getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public void setEncryptedAccessToken(String encryptedAccessToken) {
        this.encryptedAccessToken = encryptedAccessToken;
    }

    public String getEncryptedRefreshToken() {
        return encryptedRefreshToken;
    }

    public void setEncryptedRefreshToken(String encryptedRefreshToken) {
        this.encryptedRefreshToken = encryptedRefreshToken;
    }

    public Instant getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(Instant tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public Instant getLastTestedAt() {
        return lastTestedAt;
    }

    public void setLastTestedAt(Instant lastTestedAt) {
        this.lastTestedAt = lastTestedAt;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getExternalAccountDisplayName() {
        return externalAccountDisplayName;
    }

    public void setExternalAccountDisplayName(String externalAccountDisplayName) {
        this.externalAccountDisplayName = externalAccountDisplayName;
    }

    public String getExternalAccountAvatarUrl() {
        return externalAccountAvatarUrl;
    }

    public void setExternalAccountAvatarUrl(String externalAccountAvatarUrl) {
        this.externalAccountAvatarUrl = externalAccountAvatarUrl;
    }
}
