package com.willmear.sprint.jira.infrastructure.client.dto;

import java.util.List;

public record AtlassianAccessibleResourceDto(
        String id,
        String url,
        String name,
        List<String> scopes,
        String avatarUrl
) {
}
