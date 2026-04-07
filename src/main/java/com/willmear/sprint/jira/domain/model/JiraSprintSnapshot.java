package com.willmear.sprint.jira.domain.model;

import java.util.List;

public record JiraSprintSnapshot(
        JiraBoard board,
        JiraSprint sprint,
        List<JiraIssue> issues,
        List<JiraComment> comments,
        List<JiraChangelogEvent> changelogEvents
) {
}
