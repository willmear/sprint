package com.willmear.sprint.retrieval.domain.port;

import com.willmear.sprint.retrieval.application.support.SprintRetrievalSourceBundle;
import java.util.UUID;

public interface SprintRetrievalSourcePort {

    SprintRetrievalSourceBundle load(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary);
}
