package com.willmear.sprint.jira.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jira.domain.port.JiraRawPayloadRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraRawPayloadEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraRawPayloadRepository;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraRawPayloadRepositoryAdapter implements JiraRawPayloadRepositoryPort {

    private final JiraRawPayloadRepository jiraRawPayloadRepository;

    public JpaJiraRawPayloadRepositoryAdapter(JiraRawPayloadRepository jiraRawPayloadRepository) {
        this.jiraRawPayloadRepository = jiraRawPayloadRepository;
    }

    @Override
    public void save(UUID workspaceId, UUID jiraConnectionId, String payloadType, String externalReference, JsonNode payload, Instant fetchedAt) {
        JiraRawPayloadEntity entity = new JiraRawPayloadEntity();
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(workspaceId);
        JiraConnectionEntity connection = new JiraConnectionEntity();
        connection.setId(jiraConnectionId);
        entity.setWorkspace(workspace);
        entity.setJiraConnection(connection);
        entity.setPayloadType(payloadType);
        entity.setExternalReference(externalReference);
        entity.setPayload(payload);
        entity.setFetchedAt(fetchedAt);
        jiraRawPayloadRepository.save(entity);
    }
}
