package com.willmear.sprint.jira.domain.port;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public interface JiraRawPayloadRepositoryPort {

    void deleteBySyncScope(UUID workspaceId, UUID jiraConnectionId, String syncScopeType, String syncScopeReference);

    void save(
            UUID workspaceId,
            UUID jiraConnectionId,
            String payloadType,
            String externalReference,
            String syncScopeType,
            String syncScopeReference,
            JsonNode payload,
            Instant fetchedAt
    );
}
