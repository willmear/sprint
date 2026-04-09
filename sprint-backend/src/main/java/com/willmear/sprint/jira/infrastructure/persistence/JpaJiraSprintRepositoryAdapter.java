package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.port.JiraSprintRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.jira.mapper.JiraSprintMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraSprintRepositoryAdapter implements JiraSprintRepositoryPort {

    private final JiraSprintRepository jiraSprintRepository;
    private final JiraSprintMapper jiraSprintMapper;

    public JpaJiraSprintRepositoryAdapter(JiraSprintRepository jiraSprintRepository, JiraSprintMapper jiraSprintMapper) {
        this.jiraSprintRepository = jiraSprintRepository;
        this.jiraSprintMapper = jiraSprintMapper;
    }

    @Override
    public JiraSprint save(JiraSprint sprint) {
        JiraSprintEntity entity = jiraSprintRepository.findByWorkspace_IdAndExternalSprintId(
                sprint.workspaceId(),
                sprint.externalSprintId()
        ).orElseGet(JiraSprintEntity::new);
        JiraSprintEntity saved = jiraSprintRepository.save(jiraSprintMapper.updateEntity(entity, sprint));
        return jiraSprintMapper.toDomain(saved);
    }

    @Override
    public Optional<JiraSprint> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId) {
        return jiraSprintRepository.findByWorkspace_IdAndExternalSprintId(workspaceId, externalSprintId)
                .map(jiraSprintMapper::toDomain);
    }

    @Override
    public List<JiraSprint> findByWorkspaceId(UUID workspaceId) {
        return jiraSprintRepository.findByWorkspace_IdOrderBySyncedAtDesc(workspaceId).stream()
                .map(jiraSprintMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UUID> findEntityIdByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId) {
        return jiraSprintRepository.findByWorkspace_IdAndExternalSprintId(workspaceId, externalSprintId)
                .map(JiraSprintEntity::getId);
    }
}
