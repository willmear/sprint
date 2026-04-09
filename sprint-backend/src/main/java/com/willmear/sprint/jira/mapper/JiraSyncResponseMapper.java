package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.api.response.SyncSprintResponse;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import org.springframework.stereotype.Component;

@Component
public class JiraSyncResponseMapper {

    public SyncSprintResponse toResponse(SyncSprintResult result) {
        return new SyncSprintResponse(
                result.workspaceId(),
                result.jiraConnectionId(),
                result.externalSprintId(),
                result.sprintName(),
                result.issueCount(),
                result.commentCount(),
                result.changelogEventCount(),
                result.syncedAt(),
                result.status().name(),
                result.message()
        );
    }
}
