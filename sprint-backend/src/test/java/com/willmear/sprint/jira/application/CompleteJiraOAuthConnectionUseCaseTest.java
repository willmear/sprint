package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompleteJiraOAuthConnectionUseCaseTest {

    @Mock
    private JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @Mock
    private JiraOAuthClientPort jiraOAuthClientPort;
    @Mock
    private JiraClientPort jiraClientPort;
    @InjectMocks
    private CompleteJiraOAuthConnectionUseCase useCase;

    @Test
    void shouldPersistAuthorizedThenActiveConnectionOnSuccessfulCallback() {
        JiraOAuthState state = oauthState();
        JiraConnection pendingConnection = pendingConnection(state.workspaceId(), state.connectionId());
        JiraOAuthTokenResponse tokenResponse = new JiraOAuthTokenResponse("access", "refresh", Instant.now().plusSeconds(3600));

        when(jiraOAuthStateRepositoryPort.findActiveByState(eq(state.state()), any())).thenReturn(Optional.of(state));
        when(jiraConnectionRepositoryPort.findByIdAndWorkspaceId(state.connectionId(), state.workspaceId())).thenReturn(Optional.of(pendingConnection));
        when(jiraOAuthClientPort.exchangeCodeForTokens("code", state.redirectUri())).thenReturn(tokenResponse);
        when(jiraOAuthClientPort.getAccessibleResources("access")).thenReturn(List.of(
                new JiraAccessibleResource("cloud-id", "https://example.atlassian.net", "Example", List.of())
        ));
        when(jiraConnectionRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jiraClientPort.getCurrentAccount(any())).thenReturn(new JiraAccountSummary("acct-1", "Example User", "user@example.com", "https://avatar.example/user.png"));

        JiraConnection result = useCase.complete("code", state.state());

        assertThat(result.status()).isEqualTo(JiraConnectionStatus.ACTIVE);
        assertThat(result.baseUrl()).isEqualTo("https://example.atlassian.net");
        assertThat(result.externalAccountId()).isEqualTo("acct-1");
        assertThat(result.externalAccountDisplayName()).isEqualTo("Example User");
        assertThat(result.externalAccountAvatarUrl()).isEqualTo("https://avatar.example/user.png");

        ArgumentCaptor<JiraConnection> savedConnections = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort, times(2)).save(savedConnections.capture());
        assertThat(savedConnections.getAllValues().get(0).status()).isEqualTo(JiraConnectionStatus.AUTHORIZED);
        assertThat(savedConnections.getAllValues().get(1).status()).isEqualTo(JiraConnectionStatus.ACTIVE);
        verify(jiraOAuthStateRepositoryPort).save(any(JiraOAuthState.class));
    }

    @Test
    void shouldMarkConnectionFailedWhenCallbackProcessingFails() {
        JiraOAuthState state = oauthState();
        JiraConnection pendingConnection = pendingConnection(state.workspaceId(), state.connectionId());
        JiraOAuthTokenResponse tokenResponse = new JiraOAuthTokenResponse("access", "refresh", Instant.now().plusSeconds(3600));

        when(jiraOAuthStateRepositoryPort.findActiveByState(eq(state.state()), any())).thenReturn(Optional.of(state));
        when(jiraConnectionRepositoryPort.findByIdAndWorkspaceId(state.connectionId(), state.workspaceId())).thenReturn(Optional.of(pendingConnection));
        when(jiraOAuthClientPort.exchangeCodeForTokens("code", state.redirectUri())).thenReturn(tokenResponse);
        when(jiraOAuthClientPort.getAccessibleResources("access")).thenReturn(List.of(
                new JiraAccessibleResource("cloud-id", "https://example.atlassian.net", "Example", List.of())
        ));
        when(jiraConnectionRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jiraClientPort.getCurrentAccount(any())).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> useCase.complete("code", state.state()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        ArgumentCaptor<JiraConnection> savedConnections = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort, times(2)).save(savedConnections.capture());
        assertThat(savedConnections.getAllValues().get(0).status()).isEqualTo(JiraConnectionStatus.AUTHORIZED);
        assertThat(savedConnections.getAllValues().get(1).status()).isEqualTo(JiraConnectionStatus.FAILED);
        verify(jiraOAuthStateRepositoryPort).save(any(JiraOAuthState.class));
    }

    private JiraOAuthState oauthState() {
        return new JiraOAuthState(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                "http://localhost:8080/api/jira/oauth/callback",
                Instant.now().plusSeconds(900),
                false,
                Instant.now(),
                Instant.now()
        );
    }

    private JiraConnection pendingConnection(UUID workspaceId, UUID connectionId) {
        return new JiraConnection(
                connectionId,
                workspaceId,
                "https://pending-jira-site.invalid",
                JiraAuthType.OAUTH,
                JiraConnectionStatus.PENDING_AUTHORIZATION,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );
    }
}
