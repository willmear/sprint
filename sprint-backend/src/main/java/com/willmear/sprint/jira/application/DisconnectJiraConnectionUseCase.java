package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DisconnectJiraConnectionUseCase {

    private final GetJiraConnectionUseCase getJiraConnectionUseCase;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;

    public DisconnectJiraConnectionUseCase(
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort
    ) {
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
    }

    public JiraConnection disconnect(UUID workspaceId, UUID connectionId) {
        JiraConnection connection = getJiraConnectionUseCase.get(workspaceId, connectionId);
        // TODO: Revoke the remote OAuth grant before clearing stored tokens.
        return jiraConnectionRepositoryPort.save(new JiraConnection(
                connection.id(),
                connection.workspaceId(),
                connection.baseUrl(),
                connection.authType(),
                JiraConnectionStatus.REVOKED,
                connection.clientEmailOrUsername(),
                null,
                null,
                null,
                connection.lastTestedAt(),
                connection.externalAccountId(),
                connection.externalAccountDisplayName(),
                connection.externalAccountAvatarUrl(),
                connection.createdAt(),
                Instant.now()
        ));
    }
}
