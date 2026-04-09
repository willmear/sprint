package com.willmear.sprint.retrieval.application.support;

import com.willmear.sprint.common.exception.SprintDataNotFoundException;
import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.retrieval.domain.port.SprintRetrievalSourcePort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LocalSprintRetrievalSourceAdapter implements SprintRetrievalSourcePort {

    private final JiraSprintRepository jiraSprintRepository;
    private final JiraIssueRepository jiraIssueRepository;
    private final JiraCommentRepository jiraCommentRepository;

    public LocalSprintRetrievalSourceAdapter(
            JiraSprintRepository jiraSprintRepository,
            JiraIssueRepository jiraIssueRepository,
            JiraCommentRepository jiraCommentRepository
    ) {
        this.jiraSprintRepository = jiraSprintRepository;
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraCommentRepository = jiraCommentRepository;
    }

    @Override
    public SprintRetrievalSourceBundle load(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary) {
        JiraSprintEntity sprint = jiraSprintRepository.findByWorkspace_IdAndExternalSprintId(workspaceId, externalSprintId)
                .orElseThrow(() -> new SprintDataNotFoundException(workspaceId, externalSprintId));
        List<JiraIssueEntity> issues = jiraIssueRepository.findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(workspaceId, externalSprintId);
        Map<String, List<JiraCommentEntity>> commentsByIssueKey = new HashMap<>();

        if (includeComments) {
            for (JiraIssueEntity issue : issues) {
                commentsByIssueKey.put(issue.getIssueKey(), jiraCommentRepository.findByJiraIssue_IdOrderByCreatedAtExternalAsc(issue.getId()));
            }
        }

        return new SprintRetrievalSourceBundle(sprint, issues, commentsByIssueKey);
    }
}
