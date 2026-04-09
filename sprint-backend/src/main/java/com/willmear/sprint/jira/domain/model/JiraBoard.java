package com.willmear.sprint.jira.domain.model;

import java.util.UUID;

public record JiraBoard(
        Long externalBoardId,
        UUID workspaceId,
        String name,
        String boardType,
        String projectKey
) {
}
