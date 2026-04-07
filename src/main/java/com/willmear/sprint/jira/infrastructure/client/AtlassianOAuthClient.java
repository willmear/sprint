package com.willmear.sprint.jira.infrastructure.client;

import com.willmear.sprint.config.JiraOAuthProperties;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AtlassianOAuthClient implements JiraOAuthClientPort {

    private final JiraOAuthProperties jiraOAuthProperties;

    public AtlassianOAuthClient(JiraOAuthProperties jiraOAuthProperties) {
        this.jiraOAuthProperties = jiraOAuthProperties;
    }

    @Override
    public String buildAuthorizationUrl(UUID workspaceId, UUID connectionId, String state, String redirectUri) {
        // TODO: Replace this placeholder builder with real Atlassian OAuth scopes and audience parameters.
        return jiraOAuthProperties.authorizationUrl()
                + "?client_id=" + encode(jiraOAuthProperties.clientId())
                + "&redirect_uri=" + encode(redirectUri != null ? redirectUri : jiraOAuthProperties.redirectUri())
                + "&response_type=code"
                + "&state=" + encode(state)
                + "&prompt=consent"
                + "&scope=" + encode("read:jira-work offline_access")
                + "&workspace_id=" + encode(workspaceId.toString())
                + "&connection_id=" + encode(connectionId.toString());
    }

    @Override
    public JiraOAuthTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
        // TODO: Call Atlassian token endpoint and store encrypted tokens.
        String suffix = code.length() > 8 ? code.substring(0, 8) : code;
        return new JiraOAuthTokenResponse(
                "encrypted-access-token-" + suffix,
                "encrypted-refresh-token-" + suffix,
                Instant.now().plus(1, ChronoUnit.HOURS)
        );
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
