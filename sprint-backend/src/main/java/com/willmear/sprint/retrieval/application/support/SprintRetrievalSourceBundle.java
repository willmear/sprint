package com.willmear.sprint.retrieval.application.support;

import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import java.util.List;
import java.util.Map;

public record SprintRetrievalSourceBundle(
        JiraSprintEntity sprint,
        List<JiraIssueEntity> issues,
        Map<String, List<JiraCommentEntity>> commentsByIssueKey
) {
}
