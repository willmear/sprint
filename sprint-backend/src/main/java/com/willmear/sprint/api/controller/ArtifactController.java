package com.willmear.sprint.api.controller;

import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.api.response.ArtifactListResponse;
import com.willmear.sprint.artifact.api.response.ArtifactResponse;
import com.willmear.sprint.artifact.api.response.LatestArtifactResponse;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.ArtifactResponseMapper;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactResponseMapper artifactResponseMapper;

    public ArtifactController(ArtifactService artifactService, ArtifactResponseMapper artifactResponseMapper) {
        this.artifactService = artifactService;
        this.artifactResponseMapper = artifactResponseMapper;
    }

    @GetMapping("/api/artifacts/{artifactId}")
    public ResponseEntity<ArtifactResponse> getArtifact(@PathVariable UUID artifactId) {
        return ResponseEntity.ok(artifactResponseMapper.toResponse(artifactService.get(artifactId)));
    }

    @GetMapping("/api/workspaces/{workspaceId}/artifacts")
    public ResponseEntity<ArtifactListResponse> listArtifacts(
            @PathVariable UUID workspaceId,
            @RequestParam(required = false) ArtifactType artifactType,
            @RequestParam(required = false) String referenceType,
            @RequestParam(required = false) String referenceId
    ) {
        return ResponseEntity.ok(new ArtifactListResponse(
                artifactService.list(workspaceId, artifactType, referenceType, referenceId).stream()
                        .map(artifactResponseMapper::toSummaryResponse)
                        .toList()
        ));
    }

    @GetMapping("/api/workspaces/{workspaceId}/artifacts/latest")
    public ResponseEntity<LatestArtifactResponse> getLatestArtifact(
            @PathVariable UUID workspaceId,
            @RequestParam ArtifactType artifactType,
            @RequestParam String referenceType,
            @RequestParam String referenceId
    ) {
        return ResponseEntity.ok(new LatestArtifactResponse(
                artifactResponseMapper.toResponse(
                        artifactService.getLatest(workspaceId, artifactType, referenceType, referenceId)
                )
        ));
    }

    @GetMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/artifacts")
    public ResponseEntity<ArtifactListResponse> listSprintArtifacts(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @RequestParam(required = false) ArtifactType artifactType
    ) {
        return ResponseEntity.ok(new ArtifactListResponse(
                artifactService.list(workspaceId, artifactType, SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE, sprintId.toString()).stream()
                        .map(artifactResponseMapper::toSummaryResponse)
                        .toList()
        ));
    }

    @GetMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/artifacts/latest")
    public ResponseEntity<LatestArtifactResponse> getLatestSprintArtifact(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @RequestParam(defaultValue = "SPRINT_REVIEW") ArtifactType artifactType
    ) {
        return ResponseEntity.ok(new LatestArtifactResponse(
                artifactResponseMapper.toResponse(
                        artifactService.getLatest(workspaceId, artifactType, SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE, sprintId.toString())
                )
        ));
    }
}
