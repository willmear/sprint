package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TestJiraConnectionUseCase {

    private final GetJiraConnectionUseCase getJiraConnectionUseCase;
    private final JiraClientPort jiraClientPort;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;

    public TestJiraConnectionUseCase(
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            JiraClientPort jiraClientPort,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort
    ) {
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.jiraClientPort = jiraClientPort;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
    }

    public JiraConnectionTestResult test(UUID workspaceId, UUID connectionId) {
        JiraConnection connection = getJiraConnectionUseCase.get(workspaceId, connectionId);
        JiraConnectionTestResult testResult = jiraClientPort.testConnection(connection);
        jiraConnectionRepositoryPort.save(new JiraConnection(
                connection.id(),
                connection.workspaceId(),
                connection.baseUrl(),
                connection.authType(),
                testResult.success() ? JiraConnectionStatus.ACTIVE : JiraConnectionStatus.FAILED,
                connection.clientEmailOrUsername(),
                connection.encryptedAccessToken(),
                connection.encryptedRefreshToken(),
                connection.tokenExpiresAt(),
                Instant.now(),
                connection.externalAccountId(),
                connection.externalAccountDisplayName(),
                connection.externalAccountAvatarUrl(),
                connection.createdAt(),
                Instant.now()
        ));
        return testResult;
    }
}
