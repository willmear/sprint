package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.port.JiraChangelogRepositoryPort;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.mapper.JiraChangelogMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraChangelogRepositoryAdapter implements JiraChangelogRepositoryPort {

    private final JiraChangelogEventRepository jiraChangelogEventRepository;
    private final JiraChangelogMapper jiraChangelogMapper;

    public JpaJiraChangelogRepositoryAdapter(
            JiraChangelogEventRepository jiraChangelogEventRepository,
            JiraChangelogMapper jiraChangelogMapper
    ) {
        this.jiraChangelogEventRepository = jiraChangelogEventRepository;
        this.jiraChangelogMapper = jiraChangelogMapper;
    }

    @Override
    public void replaceForIssue(UUID workspaceId, UUID issueEntityId, List<JiraChangelogEvent> changelogEvents) {
        jiraChangelogEventRepository.deleteByJiraIssue_Id(issueEntityId);
        jiraChangelogEventRepository.saveAll(changelogEvents.stream()
                .map(event -> jiraChangelogMapper.toEntity(workspaceId, issueEntityId, event))
                .toList());
    }
}
