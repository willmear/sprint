package com.willmear.sprint.workspace.domain.model;

public record WorkspaceSettings(
        String defaultTimezone,
        String jiraProjectKey
) {
}
