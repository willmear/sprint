package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraConnection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JiraConnectionRepositoryPort {

    JiraConnection save(JiraConnection connection);

    List<JiraConnection> findByWorkspaceId(UUID workspaceId);

    Optional<JiraConnection> findByIdAndWorkspaceId(UUID connectionId, UUID workspaceId);

    void deleteByIdAndWorkspaceId(UUID connectionId, UUID workspaceId);
}
