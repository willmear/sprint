package com.willmear.sprint.jira.application;

import com.willmear.sprint.api.request.SyncSprintRequest;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SyncSprintUseCase {

    private final SyncSprintCoordinator syncSprintCoordinator;

    public SyncSprintUseCase(SyncSprintCoordinator syncSprintCoordinator) {
        this.syncSprintCoordinator = syncSprintCoordinator;
    }

    public SyncSprintResult sync(UUID workspaceId, UUID connectionId, Long sprintId, SyncSprintRequest request) {
        return syncSprintCoordinator.sync(workspaceId, connectionId, sprintId, request);
    }
}
