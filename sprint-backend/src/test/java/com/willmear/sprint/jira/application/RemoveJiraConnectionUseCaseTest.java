package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraOAuthStateRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraRawPayloadRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.retrieval.infrastructure.repository.EmbeddingDocumentRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveJiraConnectionUseCaseTest {

    @Mock
    private GetJiraConnectionUseCase getJiraConnectionUseCase;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @Mock
    private JiraSprintRepository jiraSprintRepository;
    @Mock
    private JiraIssueRepository jiraIssueRepository;
    @Mock
    private JiraRawPayloadRepository jiraRawPayloadRepository;
    @Mock
    private JiraOAuthStateRepository jiraOAuthStateRepository;
    @Mock
    private EmbeddingDocumentRepository embeddingDocumentRepository;
    @InjectMocks
    private RemoveJiraConnectionUseCase useCase;

    @Test
    void shouldRemoveRevokedConnectionWithoutDependentData() {
        JiraConnection connection = revokedConnection();
        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraSprintRepository.existsByJiraConnection_Id(connection.id())).thenReturn(false);
        when(jiraIssueRepository.existsByJiraConnection_Id(connection.id())).thenReturn(false);
        when(embeddingDocumentRepository.existsByJiraConnectionId(connection.id())).thenReturn(false);

        useCase.remove(connection.workspaceId(), connection.id());

        verify(jiraOAuthStateRepository).deleteByConnection_Id(connection.id());
        verify(jiraRawPayloadRepository).deleteByJiraConnection_Id(connection.id());
        verify(jiraConnectionRepositoryPort).deleteByIdAndWorkspaceId(connection.id(), connection.workspaceId());
    }

    @Test
    void shouldRejectRemoveWhenConnectionIsNotRevoked() {
        JiraConnection connection = activeConnection();
        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);

        assertThatThrownBy(() -> useCase.remove(connection.workspaceId(), connection.id()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only revoked Jira connections can be removed.");

        verify(jiraConnectionRepositoryPort, never()).deleteByIdAndWorkspaceId(connection.id(), connection.workspaceId());
    }

    @Test
    void shouldRejectRemoveWhenDependentDataExists() {
        JiraConnection connection = revokedConnection();
        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraSprintRepository.existsByJiraConnection_Id(connection.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.remove(connection.workspaceId(), connection.id()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This Jira connection still has synced sprint data and cannot be removed yet.");

        verify(jiraConnectionRepositoryPort, never()).deleteByIdAndWorkspaceId(connection.id(), connection.workspaceId());
    }

    private JiraConnection revokedConnection() {
        return new JiraConnection(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "https://example.atlassian.net",
                JiraAuthType.OAUTH,
                JiraConnectionStatus.REVOKED,
                null,
                null,
                null,
                null,
                Instant.now(),
                "acct",
                "Example User",
                "https://avatar.example/user.png",
                Instant.now(),
                Instant.now()
        );
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
                "Example User",
                "https://avatar.example/user.png",
                Instant.now(),
                Instant.now()
        );
    }
}
