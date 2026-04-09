package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import java.util.List;
import java.util.UUID;

public record SprintDataBundle(
        UUID workspaceId,
        UUID jiraConnectionId,
        JiraSprint sprint,
        List<JiraIssue> issues,
        List<JiraComment> comments,
        List<JiraChangelogEvent> changelogEvents
) {
}
