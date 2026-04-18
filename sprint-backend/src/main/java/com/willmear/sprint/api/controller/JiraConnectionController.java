package com.willmear.sprint.api.controller;

import com.willmear.sprint.auth.api.AuthService;
import com.willmear.sprint.auth.security.SessionCookieService;
import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.config.UiProperties;
import com.willmear.sprint.jira.api.JiraConnectionService;
import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.api.request.TestJiraConnectionRequest;
import com.willmear.sprint.jira.api.response.JiraConnectionResponse;
import com.willmear.sprint.jira.api.response.JiraConnectionSummaryResponse;
import com.willmear.sprint.jira.api.response.JiraConnectionTestResponse;
import com.willmear.sprint.jira.api.response.JiraOAuthStartResponse;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.mapper.JiraConnectionMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping
public class JiraConnectionController {

    private final AuthService authService;
    private final SessionCookieService sessionCookieService;
    private final JiraConnectionService jiraConnectionService;
    private final JiraConnectionMapper jiraConnectionMapper;
    private final UiProperties uiProperties;

    public JiraConnectionController(
            AuthService authService,
            SessionCookieService sessionCookieService,
            JiraConnectionService jiraConnectionService,
            JiraConnectionMapper jiraConnectionMapper,
            UiProperties uiProperties
    ) {
        this.authService = authService;
        this.sessionCookieService = sessionCookieService;
        this.jiraConnectionService = jiraConnectionService;
        this.jiraConnectionMapper = jiraConnectionMapper;
        this.uiProperties = uiProperties;
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
    public ResponseEntity<Void> completeOAuthCallback(
            @RequestParam(required = false) String code,
            @RequestParam String state,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if (authService.findActiveJiraLoginRedirect(state).isPresent()) {
            return completeAuthLoginCallback(code, state, error, errorDescription);
        }

        Optional<JiraConnectionService.OAuthCallbackContext> callbackContext = jiraConnectionService.findOAuthCallbackContext(state);

        try {
            if (StringUtils.hasText(error)) {
                jiraConnectionService.handleOAuthCallbackError(state, error, errorDescription);
            }
            if (!StringUtils.hasText(code)) {
                throw new BadRequestException("Missing OAuth authorization code.");
            }
            var connection = jiraConnectionService.completeOAuthCallback(code, state);
            URI redirectUri = buildWorkspaceRedirectUri(
                    connection.workspaceId(),
                    "success",
                    connection.id(),
                    connection.status().name(),
                    connection.externalAccountDisplayName(),
                    null
            );
            return buildRedirectResponse(redirectUri);
        } catch (RuntimeException exception) {
            URI redirectUri = callbackContext
                    .map(context -> buildWorkspaceRedirectUri(
                            context.workspaceId(),
                            "error",
                            context.connectionId(),
                            null,
                            null,
                            exception.getMessage()
                    ))
                    .orElseGet(() -> buildFallbackRedirectUri(exception.getMessage()));
            return buildRedirectResponse(redirectUri);
        }
    }

    private ResponseEntity<Void> completeAuthLoginCallback(
            String code,
            String state,
            String error,
            String errorDescription
    ) {
        if (StringUtils.hasText(error)) {
            URI redirectUri = URI.create(appendAuthStatus(authService.handleJiraLoginError(state, error, errorDescription), "error", errorDescription));
            return buildRedirectResponse(redirectUri);
        }
        if (!StringUtils.hasText(code)) {
            throw new BadRequestException("Missing OAuth authorization code.");
        }

        AuthService.LoginResult result = authService.completeJiraLogin(code, state);
        HttpHeaders headers = new HttpHeaders();
        sessionCookieService.attachSessionCookie(headers, result.session().sessionToken());
        headers.setLocation(URI.create(appendAuthStatus(result.redirectUri(), "success", null)));
        return ResponseEntity.status(302).headers(headers).build();
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

    @DeleteMapping("/api/workspaces/{workspaceId}/jira/connections/{connectionId}/remove")
    public ResponseEntity<Void> removeConnection(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId
    ) {
        jiraConnectionService.remove(workspaceId, connectionId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Void> buildRedirectResponse(URI redirectUri) {
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .build();
    }

    private URI buildWorkspaceRedirectUri(
            UUID workspaceId,
            String oauthStatus,
            UUID connectionId,
            String status,
            String accountName,
            String message
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uiProperties.allowedOrigin())
                .path("/workspaces/{workspaceId}/jira")
                .queryParam("jiraOAuth", oauthStatus);
        if (connectionId != null) {
            builder.queryParam("connectionId", connectionId);
        }
        if (StringUtils.hasText(status)) {
            builder.queryParam("status", status);
        }
        if (StringUtils.hasText(accountName)) {
            builder.queryParam("accountName", accountName);
        }
        if (StringUtils.hasText(message)) {
            builder.queryParam("message", message);
        }
        return builder.buildAndExpand(workspaceId).toUri();
    }

    private URI buildFallbackRedirectUri(String message) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uiProperties.allowedOrigin())
                .path("/workspaces")
                .queryParam("jiraOAuth", "error");
        if (StringUtils.hasText(message)) {
            builder.queryParam("message", message);
        }
        return builder.build().toUri();
    }

    private String appendAuthStatus(String redirectUri, String status, String message) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri).queryParam("auth", status);
        if (StringUtils.hasText(message)) {
            builder.queryParam("message", message);
        }
        return builder.build(true).toUriString();
    }
}
