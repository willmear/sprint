package com.willmear.sprint.jira.domain.model;

public record JiraAccountSummary(
        String accountId,
        String displayName,
        String emailAddress
) {
}
