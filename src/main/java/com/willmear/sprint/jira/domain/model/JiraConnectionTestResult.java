package com.willmear.sprint.jira.domain.model;

public record JiraConnectionTestResult(
        boolean success,
        String message,
        JiraAccountSummary accountSummary
) {
}
