package com.willmear.sprint.api.controller;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.sprintreview.api.SprintReviewService;
import com.willmear.sprint.sprintreview.api.response.GenerateSprintReviewJobResponse;
import com.willmear.sprint.sprintreview.api.response.SprintContextResponse;
import com.willmear.sprint.sprintreview.api.response.SprintReviewResponse;
import com.willmear.sprint.sprintreview.mapper.SprintContextMapper;
import com.willmear.sprint.sprintreview.mapper.SprintReviewMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/review")
public class SprintReviewController {

    private final SprintReviewService sprintReviewService;
    private final SprintReviewMapper sprintReviewMapper;
    private final SprintContextMapper sprintContextMapper;
    private final SprintReviewProperties sprintReviewProperties;

    public SprintReviewController(
            SprintReviewService sprintReviewService,
            SprintReviewMapper sprintReviewMapper,
            SprintContextMapper sprintContextMapper,
            SprintReviewProperties sprintReviewProperties
    ) {
        this.sprintReviewService = sprintReviewService;
        this.sprintReviewMapper = sprintReviewMapper;
        this.sprintContextMapper = sprintContextMapper;
        this.sprintReviewProperties = sprintReviewProperties;
    }

    @PostMapping("/generate")
    public ResponseEntity<SprintReviewResponse> generate(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @Valid @RequestBody(required = false) GenerateSprintReviewRequest request
    ) {
        GenerateSprintReviewRequest resolvedRequest = resolveRequest(request);
        return ResponseEntity.ok(
                sprintReviewMapper.toResponse(sprintReviewService.generateReview(workspaceId, sprintId, resolvedRequest))
        );
    }

    @GetMapping
    public ResponseEntity<SprintReviewResponse> getReview(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintReviewMapper.toResponse(sprintReviewService.getReview(workspaceId, sprintId)));
    }

    @GetMapping("/context")
    public ResponseEntity<SprintContextResponse> getContext(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintContextMapper.toResponse(
                sprintReviewService.getSprintContext(
                        workspaceId,
                        sprintId,
                        sprintReviewProperties.includeCommentsByDefault(),
                        sprintReviewProperties.includeChangelogByDefault()
                )
        ));
    }

    @PostMapping("/generate-job")
    public ResponseEntity<GenerateSprintReviewJobResponse> generateJob(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @Valid @RequestBody(required = false) GenerateSprintReviewRequest request
    ) {
        GenerateSprintReviewRequest resolvedRequest = resolveRequest(request);
        return ResponseEntity.accepted().body(
                sprintReviewMapper.toJobResponse(sprintReviewService.enqueueReviewGeneration(workspaceId, sprintId, resolvedRequest))
        );
    }

    private GenerateSprintReviewRequest resolveRequest(GenerateSprintReviewRequest request) {
        if (request != null) {
            return request;
        }
        return new GenerateSprintReviewRequest(
                sprintReviewProperties.includeCommentsByDefault(),
                sprintReviewProperties.includeChangelogByDefault(),
                false,
                null,
                null
        );
    }
}
