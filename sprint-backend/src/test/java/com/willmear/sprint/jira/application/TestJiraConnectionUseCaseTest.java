package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestJiraConnectionUseCaseTest {

    @Mock
    private GetJiraConnectionUseCase getJiraConnectionUseCase;
    @Mock
    private JiraClientPort jiraClientPort;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @InjectMocks
    private TestJiraConnectionUseCase useCase;

    @Test
    void shouldMarkConnectionActiveWhenTestSucceeds() {
        JiraConnection connection = activeConnection();
        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraClientPort.testConnection(connection)).thenReturn(new JiraConnectionTestResult(
                true,
                "ok",
                new JiraAccountSummary("acct", "User", "user@example.com")
        ));

        JiraConnectionTestResult result = useCase.test(connection.workspaceId(), connection.id());

        assertThat(result.success()).isTrue();
        ArgumentCaptor<JiraConnection> savedConnection = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort).save(savedConnection.capture());
        assertThat(savedConnection.getValue().status()).isEqualTo(JiraConnectionStatus.ACTIVE);
    }

    @Test
    void shouldMarkConnectionFailedWhenTestFails() {
        JiraConnection connection = activeConnection();
        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraClientPort.testConnection(connection)).thenReturn(new JiraConnectionTestResult(false, "nope", null));

        JiraConnectionTestResult result = useCase.test(connection.workspaceId(), connection.id());

        assertThat(result.success()).isFalse();
        ArgumentCaptor<JiraConnection> savedConnection = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort).save(savedConnection.capture());
        assertThat(savedConnection.getValue().status()).isEqualTo(JiraConnectionStatus.FAILED);
    }

    private JiraConnection activeConnection() {
        return new JiraConnection(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "https://example.atlassian.net",
                JiraAuthType.OAUTH,
                JiraConnectionStatus.ACTIVE,
                "user@example.com",
                "access",
                "refresh",
                Instant.now().plusSeconds(3600),
                Instant.now(),
                "acct",
                "User",
                Instant.now(),
                Instant.now()
        );
    }
}
