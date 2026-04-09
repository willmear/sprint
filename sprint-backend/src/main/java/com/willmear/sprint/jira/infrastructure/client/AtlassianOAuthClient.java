package com.willmear.sprint.jira.infrastructure.client;

import com.willmear.sprint.config.JiraOAuthProperties;
import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.infrastructure.client.dto.AtlassianAccessibleResourceDto;
import com.willmear.sprint.jira.infrastructure.client.dto.AtlassianOAuthTokenResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class AtlassianOAuthClient implements JiraOAuthClientPort {

    private static final String ATLASSIAN_AUDIENCE = "api.atlassian.com";
    private static final String OAUTH_SCOPES = String.join(
            " ",
            "offline_access",
            "read:jira-user",
            "read:jira-work",
            "read:board-scope:jira-software",
            "read:sprint:jira-software",
            "read:issue-details:jira",
            "read:jql:jira",
            "read:comment:jira",
            "read:issue.changelog:jira",
            "read:user:jira",
            "read:avatar:jira"
    );

    private final JiraOAuthProperties jiraOAuthProperties;

    public AtlassianOAuthClient(JiraOAuthProperties jiraOAuthProperties) {
        this.jiraOAuthProperties = jiraOAuthProperties;
    }

    @Override
    public String buildAuthorizationUrl(UUID workspaceId, UUID connectionId, String state, String redirectUri) {
        return jiraOAuthProperties.authorizationUrl()
                + "?client_id=" + encode(jiraOAuthProperties.clientId())
                + "&redirect_uri=" + encode(redirectUri != null ? redirectUri : jiraOAuthProperties.redirectUri())
                + "&audience=" + encode(ATLASSIAN_AUDIENCE)
                + "&response_type=code"
                + "&state=" + encode(state)
                + "&prompt=consent"
                + "&scope=" + encode(OAUTH_SCOPES);
    }

    @Override
    public JiraOAuthTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
        validateConfigured();
        try {
            AtlassianOAuthTokenResponseDto response = RestClient.create().post()
                    .uri(jiraOAuthProperties.tokenUrl())
                    .body(Map.of(
                            "grant_type", "authorization_code",
                            "client_id", jiraOAuthProperties.clientId(),
                            "client_secret", jiraOAuthProperties.clientSecret(),
                            "code", code,
                            "redirect_uri", redirectUri != null ? redirectUri : jiraOAuthProperties.redirectUri()
                    ))
                    .retrieve()
                    .body(AtlassianOAuthTokenResponseDto.class);
            if (response == null || !StringUtils.hasText(response.access_token())) {
                throw new JiraOAuthException("Atlassian token exchange returned an empty access token.");
            }
            return new JiraOAuthTokenResponse(
                    response.access_token(),
                    response.refresh_token(),
                    Instant.now().plus(response.expires_in() != null ? response.expires_in() : 3600L, ChronoUnit.SECONDS)
            );
        } catch (RuntimeException exception) {
            throw new JiraOAuthException("Atlassian OAuth token exchange failed.", exception);
        }
    }

    @Override
    public JiraOAuthTokenResponse refreshAccessToken(String refreshToken) {
        validateConfigured();
        if (!StringUtils.hasText(refreshToken)) {
            throw new JiraOAuthException("A refresh token is required to refresh the Jira OAuth access token.");
        }
        try {
            AtlassianOAuthTokenResponseDto response = RestClient.create().post()
                    .uri(jiraOAuthProperties.tokenUrl())
                    .body(Map.of(
                            "grant_type", "refresh_token",
                            "client_id", jiraOAuthProperties.clientId(),
                            "client_secret", jiraOAuthProperties.clientSecret(),
                            "refresh_token", refreshToken
                    ))
                    .retrieve()
                    .body(AtlassianOAuthTokenResponseDto.class);
            if (response == null || !StringUtils.hasText(response.access_token())) {
                throw new JiraOAuthException("Atlassian token refresh returned an empty access token.");
            }
            return new JiraOAuthTokenResponse(
                    response.access_token(),
                    StringUtils.hasText(response.refresh_token()) ? response.refresh_token() : refreshToken,
                    Instant.now().plus(response.expires_in() != null ? response.expires_in() : 3600L, ChronoUnit.SECONDS)
            );
        } catch (RuntimeException exception) {
            throw new JiraOAuthException("Atlassian OAuth token refresh failed.", exception);
        }
    }

    @Override
    public List<JiraAccessibleResource> getAccessibleResources(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new JiraOAuthException("An access token is required to resolve accessible Jira resources.");
        }
        try {
            AtlassianAccessibleResourceDto[] resources = RestClient.create().get()
                    .uri("https://api.atlassian.com/oauth/token/accessible-resources")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(AtlassianAccessibleResourceDto[].class);
            if (resources == null) {
                return List.of();
            }
            return List.of(resources).stream()
                    .map(resource -> new JiraAccessibleResource(resource.id(), resource.url(), resource.name(), resource.scopes()))
                    .toList();
        } catch (RuntimeException exception) {
            throw new JiraOAuthException("Failed to resolve accessible Jira resources.", exception);
        }
    }

    private void validateConfigured() {
        if (!StringUtils.hasText(jiraOAuthProperties.clientId()) || "replace-me".equals(jiraOAuthProperties.clientId())) {
            throw new JiraOAuthException("Jira OAuth client ID is not configured.");
        }
        if (!StringUtils.hasText(jiraOAuthProperties.clientSecret()) || "replace-me".equals(jiraOAuthProperties.clientSecret())) {
            throw new JiraOAuthException("Jira OAuth client secret is not configured.");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
