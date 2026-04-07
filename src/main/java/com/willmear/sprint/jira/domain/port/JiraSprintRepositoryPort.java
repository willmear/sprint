package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraSprint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JiraSprintRepositoryPort {

    JiraSprint save(JiraSprint sprint);

    Optional<JiraSprint> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId);

    List<JiraSprint> findByWorkspaceId(UUID workspaceId);

    Optional<UUID> findEntityIdByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId);
}
