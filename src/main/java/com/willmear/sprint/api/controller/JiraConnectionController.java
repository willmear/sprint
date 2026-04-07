package com.willmear.sprint.api.controller;

import com.willmear.sprint.jira.api.JiraConnectionService;
import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.api.request.TestJiraConnectionRequest;
import com.willmear.sprint.jira.api.response.JiraConnectionResponse;
import com.willmear.sprint.jira.api.response.JiraConnectionSummaryResponse;
import com.willmear.sprint.jira.api.response.JiraConnectionTestResponse;
import com.willmear.sprint.jira.api.response.JiraOAuthCallbackResponse;
import com.willmear.sprint.jira.api.response.JiraOAuthStartResponse;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.mapper.JiraConnectionMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class JiraConnectionController {

    private final JiraConnectionService jiraConnectionService;
    private final JiraConnectionMapper jiraConnectionMapper;

    public JiraConnectionController(JiraConnectionService jiraConnectionService, JiraConnectionMapper jiraConnectionMapper) {
        this.jiraConnectionService = jiraConnectionService;
        this.jiraConnectionMapper = jiraConnectionMapper;
    }

    @PostMapping("/api/workspaces/{workspaceId}/jira/connections/oauth/start")
    public ResponseEntity<JiraOAuthStartResponse> startOAuthConnection(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody StartJiraOAuthConnectionRequest request
    ) {
        JiraConnectionService.StartOAuthConnectionResult result = jiraConnectionService.startOAuthConnection(workspaceId, request);
        return ResponseEntity.ok(new JiraOAuthStartResponse(result.connectionId(), result.state(), result.authorizationUrl()));
    }

    @GetMapping("/api/jira/oauth/callback")
    public ResponseEntity<JiraOAuthCallbackResponse> completeOAuthCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        var connection = jiraConnectionService.completeOAuthCallback(code, state);
        return ResponseEntity.ok(new JiraOAuthCallbackResponse(
                connection.id(),
                connection.status().name(),
                connection.externalAccountDisplayName(),
                "OAuth callback processed."
        ));
    }

    @GetMapping("/api/workspaces/{workspaceId}/jira/connections")
    public ResponseEntity<List<JiraConnectionSummaryResponse>> listConnections(@PathVariable UUID workspaceId) {
        List<JiraConnectionSummaryResponse> responses = jiraConnectionService.listConnections(workspaceId).stream()
                .map(jiraConnectionMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/workspaces/{workspaceId}/jira/connections/{connectionId}")
    public ResponseEntity<JiraConnectionResponse> getConnection(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId
    ) {
        return ResponseEntity.ok(jiraConnectionMapper.toResponse(jiraConnectionService.getConnection(workspaceId, connectionId)));
    }

    @PostMapping("/api/workspaces/{workspaceId}/jira/connections/{connectionId}/test")
    public ResponseEntity<JiraConnectionTestResponse> testConnection(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId,
            @RequestBody(required = false) TestJiraConnectionRequest request
    ) {
        JiraConnectionTestResult result = jiraConnectionService.testConnection(workspaceId, connectionId);
        String accountId = result.accountSummary() != null ? result.accountSummary().accountId() : null;
        String displayName = result.accountSummary() != null ? result.accountSummary().displayName() : null;
        String emailAddress = result.accountSummary() != null ? result.accountSummary().emailAddress() : null;
        return ResponseEntity.ok(new JiraConnectionTestResponse(result.success(), result.message(), accountId, displayName, emailAddress));
    }

    @DeleteMapping("/api/workspaces/{workspaceId}/jira/connections/{connectionId}")
    public ResponseEntity<JiraConnectionResponse> disconnectConnection(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId
    ) {
        return ResponseEntity.ok(jiraConnectionMapper.toResponse(jiraConnectionService.disconnect(workspaceId, connectionId)));
    }
}
