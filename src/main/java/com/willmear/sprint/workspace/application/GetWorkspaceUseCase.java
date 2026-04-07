package com.willmear.sprint.workspace.application;

import com.willmear.sprint.common.exception.WorkspaceNotFoundException;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public GetWorkspaceUseCase(WorkspaceRepository workspaceRepository, WorkspaceMapper workspaceMapper) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public Workspace get(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .map(workspaceMapper::toDomain)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
    }
}
