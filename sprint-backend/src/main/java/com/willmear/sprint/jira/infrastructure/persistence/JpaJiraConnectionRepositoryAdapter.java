package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraConnectionRepository;
import com.willmear.sprint.jira.mapper.JiraConnectionMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraConnectionRepositoryAdapter implements JiraConnectionRepositoryPort {

    private final JiraConnectionRepository jiraConnectionRepository;
    private final JiraConnectionMapper jiraConnectionMapper;

    public JpaJiraConnectionRepositoryAdapter(
            JiraConnectionRepository jiraConnectionRepository,
            JiraConnectionMapper jiraConnectionMapper
    ) {
        this.jiraConnectionRepository = jiraConnectionRepository;
        this.jiraConnectionMapper = jiraConnectionMapper;
    }

    @Override
    public JiraConnection save(JiraConnection connection) {
        JiraConnectionEntity entity = jiraConnectionMapper.toEntity(connection);
        JiraConnectionEntity saved = jiraConnectionRepository.save(entity);
        return jiraConnectionMapper.toDomain(saved);
    }

    @Override
    public List<JiraConnection> findByWorkspaceId(UUID workspaceId) {
        return jiraConnectionRepository.findByWorkspace_IdOrderByCreatedAtDesc(workspaceId).stream()
                .map(jiraConnectionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<JiraConnection> findByIdAndWorkspaceId(UUID connectionId, UUID workspaceId) {
        return jiraConnectionRepository.findByIdAndWorkspace_Id(connectionId, workspaceId).map(jiraConnectionMapper::toDomain);
    }

    @Override
    public void deleteByIdAndWorkspaceId(UUID connectionId, UUID workspaceId) {
        jiraConnectionRepository.findByIdAndWorkspace_Id(connectionId, workspaceId)
                .ifPresent(jiraConnectionRepository::delete);
    }
}
