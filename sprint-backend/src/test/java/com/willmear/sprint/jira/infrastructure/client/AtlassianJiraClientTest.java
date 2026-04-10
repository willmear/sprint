package com.willmear.sprint.jira.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtlassianJiraClientTest {

    @Mock
    private JiraOAuthClientPort jiraOAuthClientPort;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private AtlassianJiraClient client;

    @Test
    void shouldRefreshConnectionWhenTokenIsNearExpiry() throws Exception {
        JiraConnection expiringConnection = connection(Instant.now().plusSeconds(30), "refresh-token");
        JiraOAuthTokenResponse refreshed = new JiraOAuthTokenResponse(
                "new-access-token",
                "new-refresh-token",
                Instant.now().plusSeconds(3600)
        );

        when(jiraOAuthClientPort.refreshAccessToken("refresh-token")).thenReturn(refreshed);
        when(jiraConnectionRepositoryPort.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        JiraConnection result = invokeRefreshConnectionIfExpiring(expiringConnection);

        ArgumentCaptor<JiraConnection> captor = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().encryptedAccessToken()).isEqualTo("new-access-token");
        assertThat(captor.getValue().encryptedRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.encryptedAccessToken()).isEqualTo("new-access-token");
        assertThat(result.tokenExpiresAt()).isEqualTo(refreshed.expiresAt());
    }

    @Test
    void shouldKeepConnectionWhenTokenIsStillFresh() throws Exception {
        JiraConnection freshConnection = connection(Instant.now().plusSeconds(3600), "refresh-token");

        JiraConnection result = invokeRefreshConnectionIfExpiring(freshConnection);

        assertThat(result).isSameAs(freshConnection);
    }

    @Test
    void shouldExplainMissingBoardScopesBeforeCallingJiraApi() throws Exception {
        Object resourceContext = resourceContext(
                "cloud-id",
                "https://example.atlassian.net",
                "access-token",
                List.of("read:sprint:jira-software")
        );

        assertThatThrownBy(() -> invokeEnsureScopes(
                resourceContext,
                "board discovery",
                new String[]{"read:board-scope:jira-software", "read:project:jira"}
        ))
                .rootCause()
                .isInstanceOf(JiraOAuthException.class)
                .hasMessageContaining("Missing: read:board-scope:jira-software, read:project:jira")
                .hasMessageContaining("Granted: read:sprint:jira-software");
    }

    private JiraConnection invokeRefreshConnectionIfExpiring(JiraConnection connection) throws Exception {
        Method method = AtlassianJiraClient.class.getDeclaredMethod("refreshConnectionIfExpiring", JiraConnection.class);
        method.setAccessible(true);
        return (JiraConnection) method.invoke(client, connection);
    }

    private void invokeEnsureScopes(Object resourceContext, String operation, String[] requiredScopes) throws Exception {
        Class<?> resourceContextClass = Class.forName("com.willmear.sprint.jira.infrastructure.client.AtlassianJiraClient$ResourceContext");
        Method method = AtlassianJiraClient.class.getDeclaredMethod("ensureScopes", resourceContextClass, String.class, String[].class);
        method.setAccessible(true);
        method.invoke(client, resourceContext, operation, requiredScopes);
    }

    private Object resourceContext(String cloudId, String baseUrl, String accessToken, List<String> scopes) throws Exception {
        Class<?> resourceContextClass = Class.forName("com.willmear.sprint.jira.infrastructure.client.AtlassianJiraClient$ResourceContext");
        Constructor<?> constructor = resourceContextClass.getDeclaredConstructor(String.class, String.class, String.class, List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(cloudId, baseUrl, accessToken, scopes);
    }

    private JiraConnection connection(Instant tokenExpiresAt, String refreshToken) {
        return new JiraConnection(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "https://example.atlassian.net",
                JiraAuthType.OAUTH,
                JiraConnectionStatus.ACTIVE,
                "user@example.com",
                "old-access-token",
                refreshToken,
                tokenExpiresAt,
                Instant.now(),
                "acct-1",
                "Example User",
                "https://avatar.example/user.png",
                Instant.now(),
                Instant.now()
        );
    }
}
