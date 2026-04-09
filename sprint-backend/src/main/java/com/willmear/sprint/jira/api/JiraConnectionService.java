package com.willmear.sprint.jira.api;

import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface JiraConnectionService {

    StartOAuthConnectionResult startOAuthConnection(UUID workspaceId, StartJiraOAuthConnectionRequest request);

    JiraConnection completeOAuthCallback(String code, String state);

    void handleOAuthCallbackError(String state, String error, String errorDescription);

    Optional<OAuthCallbackContext> findOAuthCallbackContext(String state);

    List<JiraConnection> listConnections(UUID workspaceId);

    JiraConnection getConnection(UUID workspaceId, UUID connectionId);

    JiraConnectionTestResult testConnection(UUID workspaceId, UUID connectionId);

    JiraConnection disconnect(UUID workspaceId, UUID connectionId);

    record StartOAuthConnectionResult(UUID connectionId, String state, String authorizationUrl) {
    }

    record OAuthCallbackContext(UUID workspaceId, UUID connectionId) {
    }
}
