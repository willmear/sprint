package com.willmear.sprint.jira.domain.model;

import java.util.List;

public record JiraAccessibleResource(
        String cloudId,
        String url,
        String name,
        List<String> scopes
) {
}
