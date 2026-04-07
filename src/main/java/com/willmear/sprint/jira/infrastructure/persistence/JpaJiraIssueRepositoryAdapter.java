package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.port.JiraIssueRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.mapper.JiraIssueMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraIssueRepositoryAdapter implements JiraIssueRepositoryPort {

    private final JiraIssueRepository jiraIssueRepository;
    private final JiraCommentRepository jiraCommentRepository;
    private final JiraChangelogEventRepository jiraChangelogEventRepository;
    private final JiraIssueMapper jiraIssueMapper;

    public JpaJiraIssueRepositoryAdapter(
            JiraIssueRepository jiraIssueRepository,
            JiraCommentRepository jiraCommentRepository,
            JiraChangelogEventRepository jiraChangelogEventRepository,
            JiraIssueMapper jiraIssueMapper
    ) {
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraCommentRepository = jiraCommentRepository;
        this.jiraChangelogEventRepository = jiraChangelogEventRepository;
        this.jiraIssueMapper = jiraIssueMapper;
    }

    @Override
    public Map<String, UUID> replaceForSprint(
            UUID workspaceId,
            UUID jiraConnectionId,
            UUID sprintEntityId,
            Long externalSprintId,
            List<JiraIssue> issues
    ) {
        List<JiraIssueEntity> existingIssues = jiraIssueRepository.findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(
                workspaceId,
                externalSprintId
        );
        for (JiraIssueEntity existingIssue : existingIssues) {
            jiraCommentRepository.deleteByJiraIssue_Id(existingIssue.getId());
            jiraChangelogEventRepository.deleteByJiraIssue_Id(existingIssue.getId());
        }
        jiraIssueRepository.deleteByJiraSprint_Id(sprintEntityId);
        List<JiraIssueEntity> saved = jiraIssueRepository.saveAll(issues.stream()
                .map(issue -> jiraIssueMapper.toEntity(workspaceId, jiraConnectionId, sprintEntityId, externalSprintId, issue))
                .toList());

        Map<String, UUID> issueIdsByKey = new LinkedHashMap<>();
        for (JiraIssueEntity entity : saved) {
            issueIdsByKey.put(entity.getIssueKey(), entity.getId());
        }
        return issueIdsByKey;
    }

    @Override
    public List<JiraIssue> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId) {
        return jiraIssueRepository.findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(workspaceId, externalSprintId).stream()
                .map(jiraIssueMapper::toDomain)
                .toList();
    }
}
