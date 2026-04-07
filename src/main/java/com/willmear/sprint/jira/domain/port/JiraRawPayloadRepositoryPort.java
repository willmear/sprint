package com.willmear.sprint.jira.domain.port;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

public interface JiraRawPayloadRepositoryPort {

    void save(UUID workspaceId, UUID jiraConnectionId, String payloadType, String externalReference, JsonNode payload, Instant fetchedAt);
}
