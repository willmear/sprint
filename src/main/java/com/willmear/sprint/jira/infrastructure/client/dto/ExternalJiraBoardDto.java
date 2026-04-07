package com.willmear.sprint.jira.infrastructure.client.dto;

public record ExternalJiraBoardDto(
        Long id,
        String name,
        String type,
        String projectKey
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}
