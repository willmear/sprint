package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.jira.api.JiraConnectionService.StartOAuthConnectionResult;
import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartJiraOAuthConnectionUseCaseTest {

    @Mock
    private WorkspaceService workspaceService;
    @Mock
    private JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    @Mock
    private JiraOAuthStateRepositoryPort jiraOAuthStateRepositoryPort;
    @Mock
    private JiraOAuthClientPort jiraOAuthClientPort;
    @InjectMocks
    private StartJiraOAuthConnectionUseCase useCase;

    @Test
    void shouldPersistPendingConnectionAndBuildAuthorizationUrlWithoutBaseUrl() {
        UUID workspaceId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        StartJiraOAuthConnectionRequest request = new StartJiraOAuthConnectionRequest(null, "http://localhost:8080/api/jira/oauth/callback");

        when(jiraConnectionRepositoryPort.save(any())).thenAnswer(invocation -> {
            JiraConnection connection = invocation.getArgument(0);
            return new JiraConnection(
                    connectionId,
                    connection.workspaceId(),
                    connection.baseUrl(),
                    connection.authType(),
                    connection.status(),
                    connection.clientEmailOrUsername(),
                    connection.encryptedAccessToken(),
                    connection.encryptedRefreshToken(),
                    connection.tokenExpiresAt(),
                    connection.lastTestedAt(),
                    connection.externalAccountId(),
                    connection.externalAccountDisplayName(),
                    connection.createdAt(),
                    connection.updatedAt()
            );
        });
        when(jiraOAuthClientPort.buildAuthorizationUrl(any(), any(), any(), any())).thenReturn("https://auth.example");

        StartOAuthConnectionResult result = useCase.start(workspaceId, request);

        assertThat(result.connectionId()).isEqualTo(connectionId);
        assertThat(result.authorizationUrl()).isEqualTo("https://auth.example");

        ArgumentCaptor<JiraConnection> connectionCaptor = ArgumentCaptor.forClass(JiraConnection.class);
        verify(jiraConnectionRepositoryPort).save(connectionCaptor.capture());
        assertThat(connectionCaptor.getValue().baseUrl()).isEqualTo("https://pending-jira-site.invalid");

        verify(jiraOAuthStateRepositoryPort).save(any(JiraOAuthState.class));
    }
}
