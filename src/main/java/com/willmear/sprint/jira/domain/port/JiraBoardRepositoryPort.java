package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraBoard;
import java.util.Optional;
import java.util.UUID;

public interface JiraBoardRepositoryPort {

    JiraBoard save(JiraBoard board);

    Optional<JiraBoard> findByWorkspaceIdAndExternalBoardId(UUID workspaceId, Long externalBoardId);
}
