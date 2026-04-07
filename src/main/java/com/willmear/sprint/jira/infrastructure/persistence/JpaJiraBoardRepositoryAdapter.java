package com.willmear.sprint.jira.infrastructure.persistence;

import com.willmear.sprint.jira.domain.model.JiraBoard;
import com.willmear.sprint.jira.domain.port.JiraBoardRepositoryPort;
import com.willmear.sprint.jira.infrastructure.entity.JiraBoardEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraBoardRepository;
import com.willmear.sprint.jira.mapper.JiraBoardMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JpaJiraBoardRepositoryAdapter implements JiraBoardRepositoryPort {

    private final JiraBoardRepository jiraBoardRepository;
    private final JiraBoardMapper jiraBoardMapper;

    public JpaJiraBoardRepositoryAdapter(JiraBoardRepository jiraBoardRepository, JiraBoardMapper jiraBoardMapper) {
        this.jiraBoardRepository = jiraBoardRepository;
        this.jiraBoardMapper = jiraBoardMapper;
    }

    @Override
    public JiraBoard save(JiraBoard board) {
        JiraBoardEntity entity = jiraBoardRepository.findByWorkspace_IdAndExternalBoardId(board.workspaceId(), board.externalBoardId())
                .orElseGet(JiraBoardEntity::new);
        JiraBoardEntity saved = jiraBoardRepository.save(jiraBoardMapper.updateEntity(entity, board));
        return jiraBoardMapper.toDomain(saved);
    }

    @Override
    public Optional<JiraBoard> findByWorkspaceIdAndExternalBoardId(UUID workspaceId, Long externalBoardId) {
        return jiraBoardRepository.findByWorkspace_IdAndExternalBoardId(workspaceId, externalBoardId)
                .map(jiraBoardMapper::toDomain);
    }
}
