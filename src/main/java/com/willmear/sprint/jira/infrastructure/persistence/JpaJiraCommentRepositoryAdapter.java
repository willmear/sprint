package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.port.JiraCommentRepositoryPort;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.mapper.JiraCommentMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraCommentRepositoryAdapter implements JiraCommentRepositoryPort {

    private final JiraCommentRepository jiraCommentRepository;
    private final JiraCommentMapper jiraCommentMapper;

    public JpaJiraCommentRepositoryAdapter(JiraCommentRepository jiraCommentRepository, JiraCommentMapper jiraCommentMapper) {
        this.jiraCommentRepository = jiraCommentRepository;
        this.jiraCommentMapper = jiraCommentMapper;
    }

    @Override
    public void replaceForIssue(UUID workspaceId, UUID issueEntityId, List<JiraComment> comments) {
        jiraCommentRepository.deleteByJiraIssue_Id(issueEntityId);
        jiraCommentRepository.saveAll(comments.stream()
                .map(comment -> jiraCommentMapper.toEntity(workspaceId, issueEntityId, comment))
                .toList());
    }
}
