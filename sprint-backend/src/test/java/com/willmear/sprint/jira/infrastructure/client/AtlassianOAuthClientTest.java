package com.willmear.sprint.jira.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.config.JiraOAuthProperties;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AtlassianOAuthClientTest {

    @Test
    void shouldRequestProjectScopeForBoardDiscovery() {
        AtlassianOAuthClient client = new AtlassianOAuthClient(new JiraOAuthProperties(
                "client-id",
                "client-secret",
                "https://auth.atlassian.com/authorize",
                "https://auth.atlassian.com/oauth/token",
                "http://localhost:8080/api/jira/oauth/callback"
        ));

        String authorizationUrl = client.buildAuthorizationUrl(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "state",
                "http://localhost:8080/api/jira/oauth/callback"
        );

        assertThat(authorizationUrl).contains("read%3Aproject%3Ajira");
    }

    @Test
    void shouldRejectRefreshWhenRefreshTokenIsMissing() {
        AtlassianOAuthClient client = new AtlassianOAuthClient(new JiraOAuthProperties(
                "client-id",
                "client-secret",
                "https://auth.atlassian.com/authorize",
                "https://auth.atlassian.com/oauth/token",
                "http://localhost:8080/api/jira/oauth/callback"
        ));

        assertThatThrownBy(() -> client.refreshAccessToken(" "))
                .isInstanceOf(JiraOAuthException.class)
                .hasMessage("A refresh token is required to refresh the Jira OAuth access token.");
    }
}
