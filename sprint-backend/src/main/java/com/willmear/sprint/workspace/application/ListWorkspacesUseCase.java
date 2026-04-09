package com.willmear.sprint.workspace.application;

import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ListWorkspacesUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public ListWorkspacesUseCase(WorkspaceRepository workspaceRepository, WorkspaceMapper workspaceMapper) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
    }

    public List<Workspace> list() {
        return workspaceRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(workspaceMapper::toDomain)
                .toList();
    }
}
