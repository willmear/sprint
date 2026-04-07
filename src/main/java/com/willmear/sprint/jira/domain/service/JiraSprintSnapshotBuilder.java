package com.willmear.sprint.jira.domain.service;

import com.willmear.sprint.jira.domain.model.JiraBoard;
import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.model.JiraSprintSnapshot;
import com.willmear.sprint.jira.domain.model.SprintSyncStatus;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraSprintSnapshotBuilder {

    public JiraSprintSnapshot build(
            JiraBoard board,
            JiraSprint sprint,
            List<JiraIssue> issues,
            List<JiraComment> comments,
            List<JiraChangelogEvent> changelogEvents
    ) {
        return new JiraSprintSnapshot(board, sprint, issues, comments, changelogEvents);
    }

    public SyncSprintResult buildResult(
            UUID workspaceId,
            UUID jiraConnectionId,
            JiraSprintSnapshot snapshot,
            Instant syncedAt,
            SprintSyncStatus status,
            String message
    ) {
        return new SyncSprintResult(
                workspaceId,
                jiraConnectionId,
                snapshot.sprint().externalSprintId(),
                snapshot.sprint().name(),
                snapshot.issues().size(),
                snapshot.comments().size(),
                snapshot.changelogEvents().size(),
                syncedAt,
                status,
                message
        );
    }
}
