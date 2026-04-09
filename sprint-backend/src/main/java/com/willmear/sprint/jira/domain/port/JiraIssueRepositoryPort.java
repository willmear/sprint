package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraIssue;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JiraIssueRepositoryPort {

    Map<String, UUID> replaceForSprint(
            UUID workspaceId,
            UUID jiraConnectionId,
            UUID sprintEntityId,
            Long externalSprintId,
            List<JiraIssue> issues
    );

    List<JiraIssue> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId);
}
