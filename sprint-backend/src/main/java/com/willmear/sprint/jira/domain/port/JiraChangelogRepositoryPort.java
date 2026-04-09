package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import java.util.List;
import java.util.UUID;

public interface JiraChangelogRepositoryPort {

    void replaceForIssue(UUID workspaceId, UUID issueEntityId, List<JiraChangelogEvent> changelogEvents);
}
