package com.willmear.sprint.jira.api;

import com.willmear.sprint.api.request.SyncSprintRequest;
import com.willmear.sprint.jira.domain.model.AvailableJiraSprint;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import java.util.List;
import java.util.UUID;

public interface JiraSyncService {

    SyncSprintResult syncSprint(UUID workspaceId, UUID connectionId, Long sprintId, SyncSprintRequest request);

    List<AvailableJiraSprint> listAvailableSprints(UUID workspaceId, UUID connectionId);

    List<JiraSprint> listSprints(UUID workspaceId);

    JiraSprint getSprint(UUID workspaceId, Long sprintId);

    List<JiraIssue> getSprintIssues(UUID workspaceId, Long sprintId);
}
