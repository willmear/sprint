package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.common.exception.JiraOAuthCallbackException;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FailJiraOAuthConnectionUseCaseTest {

    @Mock
    private JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @InjectMocks
    private FailJiraOAuthConnectionUseCase useCase;

    @Test
    void shouldMarkConnectionFailedAndConsumeState() {
        JiraOAuthState state = new JiraOAuthState(
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
        JiraConnection connection = new JiraConnection(
                state.connectionId(),
                state.workspaceId(),
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
                Instant.now(),
                Instant.now()
        );

        when(jiraOAuthStateRepositoryPort.findActiveByState(eq(state.state()), any())).thenReturn(Optional.of(state));
        when(jiraConnectionRepositoryPort.findByIdAndWorkspaceId(state.connectionId(), state.workspaceId())).thenReturn(Optional.of(connection));

        assertThatThrownBy(() -> useCase.fail(state.state(), "access_denied", "User denied access"))
                .isInstanceOf(JiraOAuthCallbackException.class)
                .hasMessageContaining("User denied access");

        ArgumentCaptor<JiraConnection> savedConnection = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort).save(savedConnection.capture());
        assertThat(savedConnection.getValue().status()).isEqualTo(JiraConnectionStatus.FAILED);
        verify(jiraOAuthStateRepositoryPort).save(any(JiraOAuthState.class));
    }
}
