package com.willmear.sprint.api.controller;

import com.willmear.sprint.api.request.SyncSprintRequest;
import com.willmear.sprint.api.response.AvailableJiraSprintResponse;
import com.willmear.sprint.api.response.IssueResponse;
import com.willmear.sprint.api.response.SprintResponse;
import com.willmear.sprint.api.response.SprintSummaryResponse;
import com.willmear.sprint.api.response.SyncSprintResponse;
import com.willmear.sprint.jira.api.JiraSyncService;
import com.willmear.sprint.jira.mapper.AvailableJiraSprintMapper;
import com.willmear.sprint.jira.mapper.JiraIssueMapper;
import com.willmear.sprint.jira.mapper.JiraSprintMapper;
import com.willmear.sprint.jira.mapper.JiraSyncResponseMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}")
public class SprintController {

    private final JiraSyncService jiraSyncService;
    private final JiraSyncResponseMapper jiraSyncResponseMapper;
    private final AvailableJiraSprintMapper availableJiraSprintMapper;
    private final JiraSprintMapper jiraSprintMapper;
    private final JiraIssueMapper jiraIssueMapper;

    public SprintController(
            JiraSyncService jiraSyncService,
            JiraSyncResponseMapper jiraSyncResponseMapper,
            AvailableJiraSprintMapper availableJiraSprintMapper,
            JiraSprintMapper jiraSprintMapper,
            JiraIssueMapper jiraIssueMapper
    ) {
        this.jiraSyncService = jiraSyncService;
        this.jiraSyncResponseMapper = jiraSyncResponseMapper;
        this.availableJiraSprintMapper = availableJiraSprintMapper;
        this.jiraSprintMapper = jiraSprintMapper;
        this.jiraIssueMapper = jiraIssueMapper;
    }

    @PostMapping("/jira/connections/{connectionId}/sprints/{sprintId}/sync")
    public ResponseEntity<SyncSprintResponse> syncSprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId,
            @PathVariable Long sprintId,
            @Valid @RequestBody(required = false) SyncSprintRequest request
    ) {
        SyncSprintRequest syncRequest = request != null ? request : new SyncSprintRequest(null, true, true);
        return ResponseEntity.ok(jiraSyncResponseMapper.toResponse(
                jiraSyncService.syncSprint(workspaceId, connectionId, sprintId, syncRequest)
        ));
    }

    @GetMapping("/jira/connections/{connectionId}/available-sprints")
    public ResponseEntity<List<AvailableJiraSprintResponse>> listAvailableSprints(
            @PathVariable UUID workspaceId,
            @PathVariable UUID connectionId
    ) {
        List<AvailableJiraSprintResponse> responses = jiraSyncService.listAvailableSprints(workspaceId, connectionId).stream()
                .map(availableJiraSprintMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sprints")
    public ResponseEntity<List<SprintSummaryResponse>> listSprints(@PathVariable UUID workspaceId) {
        List<SprintSummaryResponse> responses = jiraSyncService.listSprints(workspaceId).stream()
                .map(jiraSprintMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sprints/{sprintId}")
    public ResponseEntity<SprintResponse> getSprint(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        int issueCount = jiraSyncService.getSprintIssues(workspaceId, sprintId).size();
        return ResponseEntity.ok(jiraSprintMapper.toResponse(jiraSyncService.getSprint(workspaceId, sprintId), issueCount));
    }

    @GetMapping("/sprints/{sprintId}/issues")
    public ResponseEntity<List<IssueResponse>> getSprintIssues(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        List<IssueResponse> responses = jiraSyncService.getSprintIssues(workspaceId, sprintId).stream()
                .map(jiraIssueMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
