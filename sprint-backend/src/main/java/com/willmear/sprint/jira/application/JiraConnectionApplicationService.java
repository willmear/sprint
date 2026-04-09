package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.api.JiraConnectionService;
import com.willmear.sprint.jira.api.request.StartJiraOAuthConnectionRequest;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JiraConnectionApplicationService implements JiraConnectionService {

    private final StartJiraOAuthConnectionUseCase startJiraOAuthConnectionUseCase;
    private final CompleteJiraOAuthConnectionUseCase completeJiraOAuthConnectionUseCase;
    private final FailJiraOAuthConnectionUseCase failJiraOAuthConnectionUseCase;
    private final GetOAuthCallbackContextUseCase getOAuthCallbackContextUseCase;
    private final GetJiraConnectionUseCase getJiraConnectionUseCase;
    private final ListJiraConnectionsUseCase listJiraConnectionsUseCase;
    private final TestJiraConnectionUseCase testJiraConnectionUseCase;
    private final DisconnectJiraConnectionUseCase disconnectJiraConnectionUseCase;

    public JiraConnectionApplicationService(
            StartJiraOAuthConnectionUseCase startJiraOAuthConnectionUseCase,
            CompleteJiraOAuthConnectionUseCase completeJiraOAuthConnectionUseCase,
            FailJiraOAuthConnectionUseCase failJiraOAuthConnectionUseCase,
            GetOAuthCallbackContextUseCase getOAuthCallbackContextUseCase,
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            ListJiraConnectionsUseCase listJiraConnectionsUseCase,
            TestJiraConnectionUseCase testJiraConnectionUseCase,
            DisconnectJiraConnectionUseCase disconnectJiraConnectionUseCase
    ) {
        this.startJiraOAuthConnectionUseCase = startJiraOAuthConnectionUseCase;
        this.completeJiraOAuthConnectionUseCase = completeJiraOAuthConnectionUseCase;
        this.failJiraOAuthConnectionUseCase = failJiraOAuthConnectionUseCase;
        this.getOAuthCallbackContextUseCase = getOAuthCallbackContextUseCase;
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.listJiraConnectionsUseCase = listJiraConnectionsUseCase;
        this.testJiraConnectionUseCase = testJiraConnectionUseCase;
        this.disconnectJiraConnectionUseCase = disconnectJiraConnectionUseCase;
    }

    @Override
    public StartOAuthConnectionResult startOAuthConnection(UUID workspaceId, StartJiraOAuthConnectionRequest request) {
        return startJiraOAuthConnectionUseCase.start(workspaceId, request);
    }

    @Override
    public JiraConnection completeOAuthCallback(String code, String state) {
        return completeJiraOAuthConnectionUseCase.complete(code, state);
    }

    @Override
    public void handleOAuthCallbackError(String state, String error, String errorDescription) {
        failJiraOAuthConnectionUseCase.fail(state, error, errorDescription);
    }

    @Override
    public Optional<OAuthCallbackContext> findOAuthCallbackContext(String state) {
        return getOAuthCallbackContextUseCase.find(state);
    }

    @Override
    public List<JiraConnection> listConnections(UUID workspaceId) {
        return listJiraConnectionsUseCase.list(workspaceId);
    }

    @Override
    public JiraConnection getConnection(UUID workspaceId, UUID connectionId) {
        return getJiraConnectionUseCase.get(workspaceId, connectionId);
    }

    @Override
    public JiraConnectionTestResult testConnection(UUID workspaceId, UUID connectionId) {
        return testJiraConnectionUseCase.test(workspaceId, connectionId);
    }

    @Override
    public JiraConnection disconnect(UUID workspaceId, UUID connectionId) {
        return disconnectJiraConnectionUseCase.disconnect(workspaceId, connectionId);
    }
}
