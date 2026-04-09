package com.willmear.sprint.api.controller;

import com.willmear.sprint.api.request.CreateWorkspaceRequest;
import com.willmear.sprint.api.response.WorkspaceResponse;
import com.willmear.sprint.api.response.WorkspaceSummaryResponse;
import com.willmear.sprint.workspace.api.WorkspaceService;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    public WorkspaceController(WorkspaceService workspaceService, WorkspaceMapper workspaceMapper) {
        this.workspaceService = workspaceService;
        this.workspaceMapper = workspaceMapper;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        WorkspaceResponse response = workspaceMapper.toResponse(
                workspaceService.createWorkspace(request.name(), request.description())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable UUID workspaceId) {
        return ResponseEntity.ok(workspaceMapper.toResponse(workspaceService.getWorkspace(workspaceId)));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceSummaryResponse>> listWorkspaces() {
        List<WorkspaceSummaryResponse> responses = workspaceService.listWorkspaces().stream()
                .map(workspaceMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
