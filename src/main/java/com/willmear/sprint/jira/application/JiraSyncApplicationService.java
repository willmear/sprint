package com.willmear.sprint.jira.application;

import com.willmear.sprint.api.request.SyncSprintRequest;
import com.willmear.sprint.jira.api.JiraSyncService;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JiraSyncApplicationService implements JiraSyncService {

    private final SyncSprintUseCase syncSprintUseCase;
    private final ListSprintsUseCase listSprintsUseCase;
    private final GetSprintUseCase getSprintUseCase;
    private final GetSprintIssuesUseCase getSprintIssuesUseCase;

    public JiraSyncApplicationService(
            SyncSprintUseCase syncSprintUseCase,
            ListSprintsUseCase listSprintsUseCase,
            GetSprintUseCase getSprintUseCase,
            GetSprintIssuesUseCase getSprintIssuesUseCase
    ) {
        this.syncSprintUseCase = syncSprintUseCase;
        this.listSprintsUseCase = listSprintsUseCase;
        this.getSprintUseCase = getSprintUseCase;
        this.getSprintIssuesUseCase = getSprintIssuesUseCase;
    }

    @Override
    public SyncSprintResult syncSprint(UUID workspaceId, UUID connectionId, Long sprintId, SyncSprintRequest request) {
        return syncSprintUseCase.sync(workspaceId, connectionId, sprintId, request);
    }

    @Override
    public List<JiraSprint> listSprints(UUID workspaceId) {
        return listSprintsUseCase.list(workspaceId);
    }

    @Override
    public JiraSprint getSprint(UUID workspaceId, Long sprintId) {
        return getSprintUseCase.get(workspaceId, sprintId);
    }

    @Override
    public List<JiraIssue> getSprintIssues(UUID workspaceId, Long sprintId) {
        return getSprintIssuesUseCase.get(workspaceId, sprintId);
    }
}
