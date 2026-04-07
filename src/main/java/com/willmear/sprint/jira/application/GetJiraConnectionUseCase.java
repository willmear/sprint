package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.JiraConnectionNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetJiraConnectionUseCase {

    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;

    public GetJiraConnectionUseCase(JiraConnectionRepositoryPort jiraConnectionRepositoryPort) {
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
    }

    public JiraConnection get(UUID workspaceId, UUID connectionId) {
        return jiraConnectionRepositoryPort.findByIdAndWorkspaceId(connectionId, workspaceId)
                .orElseThrow(() -> new JiraConnectionNotFoundException(workspaceId, connectionId));
    }
}
