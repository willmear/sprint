package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraComment;
import java.util.List;
import java.util.UUID;

public interface JiraCommentRepositoryPort {

    void replaceForIssue(UUID workspaceId, UUID issueEntityId, List<JiraComment> comments);
}
