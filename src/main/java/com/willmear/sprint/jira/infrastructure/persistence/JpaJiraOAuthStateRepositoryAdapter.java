package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import com.willmear.sprint.jira.domain.port.JiraOAuthStateRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraOAuthStateEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraOAuthStateRepository;
import com.willmear.sprint.jira.mapper.JiraOAuthStateMapper;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraOAuthStateRepositoryAdapter implements JiraOAuthStateRepositoryPort {

    private final JiraOAuthStateRepository jiraOAuthStateRepository;
    private final JiraOAuthStateMapper jiraOAuthStateMapper;

    public JpaJiraOAuthStateRepositoryAdapter(
            JiraOAuthStateRepository jiraOAuthStateRepository,
            JiraOAuthStateMapper jiraOAuthStateMapper
    ) {
        this.jiraOAuthStateRepository = jiraOAuthStateRepository;
        this.jiraOAuthStateMapper = jiraOAuthStateMapper;
    }

    @Override
    public JiraOAuthState save(JiraOAuthState jiraOAuthState) {
        JiraOAuthStateEntity entity = jiraOAuthStateMapper.toEntity(jiraOAuthState);
        JiraOAuthStateEntity saved = jiraOAuthStateRepository.save(entity);
        return jiraOAuthStateMapper.toDomain(saved);
    }

    @Override
    public Optional<JiraOAuthState> findActiveByState(String state, Instant now) {
        return jiraOAuthStateRepository.findActiveByState(state, now).map(jiraOAuthStateMapper::toDomain);
    }
}
