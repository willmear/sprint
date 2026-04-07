package com.willmear.sprint.jira.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.api.request.SyncSprintRequest;
import com.willmear.sprint.common.exception.JiraConnectionUnauthorizedException;
import com.willmear.sprint.common.exception.JiraSyncException;
import com.willmear.sprint.config.JiraSyncProperties;
import com.willmear.sprint.jira.domain.model.JiraBoard;
import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.domain.model.JiraSprintSnapshot;
import com.willmear.sprint.jira.domain.model.SprintSyncStatus;
import com.willmear.sprint.jira.domain.model.SyncSprintResult;
import com.willmear.sprint.jira.domain.port.JiraBoardRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraChangelogRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraCommentRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraIssueRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraRawPayloadRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraSprintRepositoryPort;
import com.willmear.sprint.jira.domain.service.JiraIssueNormaliser;
import com.willmear.sprint.jira.domain.service.JiraSprintSnapshotBuilder;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraChangelogDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraCommentDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.logging.MdcKeys;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SyncSprintCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSprintCoordinator.class);

    private final WorkspaceService workspaceService;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final JiraClientPort jiraClientPort;
    private final JiraBoardRepositoryPort jiraBoardRepositoryPort;
    private final JiraSprintRepositoryPort jiraSprintRepositoryPort;
    private final JiraIssueRepositoryPort jiraIssueRepositoryPort;
    private final JiraCommentRepositoryPort jiraCommentRepositoryPort;
    private final JiraChangelogRepositoryPort jiraChangelogRepositoryPort;
    private final JiraRawPayloadRepositoryPort jiraRawPayloadRepositoryPort;
    private final JiraIssueNormaliser jiraIssueNormaliser;
    private final JiraSprintSnapshotBuilder jiraSprintSnapshotBuilder;
    private final ObjectMapper objectMapper;
    private final JiraSyncProperties jiraSyncProperties;
    private final LoggingContextHelper loggingContextHelper;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public SyncSprintCoordinator(
            WorkspaceService workspaceService,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            JiraClientPort jiraClientPort,
            JiraBoardRepositoryPort jiraBoardRepositoryPort,
            JiraSprintRepositoryPort jiraSprintRepositoryPort,
            JiraIssueRepositoryPort jiraIssueRepositoryPort,
            JiraCommentRepositoryPort jiraCommentRepositoryPort,
            JiraChangelogRepositoryPort jiraChangelogRepositoryPort,
            JiraRawPayloadRepositoryPort jiraRawPayloadRepositoryPort,
            JiraIssueNormaliser jiraIssueNormaliser,
            JiraSprintSnapshotBuilder jiraSprintSnapshotBuilder,
            ObjectMapper objectMapper,
            JiraSyncProperties jiraSyncProperties,
            LoggingContextHelper loggingContextHelper,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.workspaceService = workspaceService;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.jiraClientPort = jiraClientPort;
        this.jiraBoardRepositoryPort = jiraBoardRepositoryPort;
        this.jiraSprintRepositoryPort = jiraSprintRepositoryPort;
        this.jiraIssueRepositoryPort = jiraIssueRepositoryPort;
        this.jiraCommentRepositoryPort = jiraCommentRepositoryPort;
        this.jiraChangelogRepositoryPort = jiraChangelogRepositoryPort;
        this.jiraRawPayloadRepositoryPort = jiraRawPayloadRepositoryPort;
        this.jiraIssueNormaliser = jiraIssueNormaliser;
        this.jiraSprintSnapshotBuilder = jiraSprintSnapshotBuilder;
        this.objectMapper = objectMapper;
        this.jiraSyncProperties = jiraSyncProperties;
        this.loggingContextHelper = loggingContextHelper;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    public SyncSprintResult sync(UUID workspaceId, UUID connectionId, Long sprintId, SyncSprintRequest request) {
        long startedAt = System.nanoTime();
        try (LoggingContextHelper.Scope ignored = loggingContextHelper.putAll(Map.of(
                MdcKeys.WORKSPACE_ID, workspaceId,
                MdcKeys.SPRINT_ID, sprintId
        )); TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.JIRA_SYNC)) {
            workflowMetricsRecorder.increment("jira.sync.started");
            LOGGER.info("jira.sync.start workspaceId={} connectionId={} sprintId={}", workspaceId, connectionId, sprintId);
            workspaceService.getWorkspace(workspaceId);
            JiraConnection connection = jiraConnectionRepositoryPort.findByIdAndWorkspaceId(connectionId, workspaceId)
                    .orElseThrow(() -> new JiraSyncException("Jira connection not found for sprint sync."));
            if (connection.status() != JiraConnectionStatus.ACTIVE && connection.status() != JiraConnectionStatus.AUTHORIZED) {
                throw new JiraConnectionUnauthorizedException(workspaceId, connectionId);
            }

            Instant syncedAt = Instant.now();
            ExternalJiraSprintDto sprintDto = jiraClientPort.fetchSprint(connection, sprintId);
            jiraRawPayloadRepositoryPort.save(
                    workspaceId,
                    connectionId,
                    "SPRINT",
                    String.valueOf(sprintId),
                    objectMapper.valueToTree(sprintDto),
                    syncedAt
            );

            Long boardId = request.boardId() != null ? request.boardId() : sprintDto.boardId();
            JiraBoard board = null;
            if (boardId != null) {
                ExternalJiraBoardDto boardDto = jiraClientPort.fetchBoard(connection, boardId);
                jiraRawPayloadRepositoryPort.save(
                        workspaceId,
                        connectionId,
                        "BOARD",
                        String.valueOf(boardId),
                        objectMapper.valueToTree(boardDto),
                        syncedAt
                );
                board = jiraBoardRepositoryPort.save(jiraIssueNormaliser.normaliseBoard(workspaceId, boardDto));
            }

            JiraSprint sprint = jiraSprintRepositoryPort.save(
                    jiraIssueNormaliser.normaliseSprint(workspaceId, connectionId, sprintDto, syncedAt)
            );

            List<ExternalJiraIssueDto> issueDtos = jiraClientPort.fetchSprintIssues(connection, sprintId);
            List<JiraIssue> issues = issueDtos.stream()
                    .map(dto -> jiraIssueNormaliser.normaliseIssue(workspaceId, connectionId, sprintId, dto))
                    .toList();
            for (ExternalJiraIssueDto issueDto : issueDtos) {
                jiraRawPayloadRepositoryPort.save(
                        workspaceId,
                        connectionId,
                        "ISSUE",
                        issueDto.key(),
                        objectMapper.valueToTree(issueDto),
                        syncedAt
                );
            }

            UUID sprintEntityId = jiraSprintRepositoryPort.findEntityIdByWorkspaceIdAndExternalSprintId(workspaceId, sprintId)
                    .orElseThrow(() -> new JiraSyncException("Persisted sprint row could not be resolved after sync."));
            Map<String, UUID> issueIdsByKey = jiraIssueRepositoryPort.replaceForSprint(
                    workspaceId,
                    connectionId,
                    sprintEntityId,
                    sprintId,
                    issues
            );

            boolean includeComments = request.includeComments() != null
                    ? request.includeComments()
                    : jiraSyncProperties.fetchComments();
            boolean includeChangelog = request.includeChangelog() != null
                    ? request.includeChangelog()
                    : jiraSyncProperties.fetchChangelog();
            List<JiraComment> comments = new ArrayList<>();
            List<JiraChangelogEvent> changelogEvents = new ArrayList<>();

            for (JiraIssue issue : issues) {
                UUID issueEntityId = issueIdsByKey.get(issue.issueKey());
                if (issueEntityId == null) {
                    throw new JiraSyncException("Failed to resolve persisted issue id for " + issue.issueKey());
                }

                if (includeComments) {
                    List<ExternalJiraCommentDto> commentDtos = jiraClientPort.fetchIssueComments(connection, issue.issueKey());
                    List<JiraComment> issueComments = commentDtos.stream()
                            .map(dto -> jiraIssueNormaliser.normaliseComment(workspaceId, issue.issueKey(), dto))
                            .toList();
                    jiraCommentRepositoryPort.replaceForIssue(workspaceId, issueEntityId, issueComments);
                    comments.addAll(issueComments);
                    for (ExternalJiraCommentDto commentDto : commentDtos) {
                        jiraRawPayloadRepositoryPort.save(
                                workspaceId,
                                connectionId,
                                "COMMENT",
                                commentDto.id(),
                                objectMapper.valueToTree(commentDto),
                                syncedAt
                        );
                    }
                }

                if (includeChangelog) {
                    List<ExternalJiraChangelogDto> changelogDtos = jiraClientPort.fetchIssueChangelog(connection, issue.issueKey());
                    List<JiraChangelogEvent> issueChangelog = changelogDtos.stream()
                            .map(dto -> jiraIssueNormaliser.normaliseChangelog(workspaceId, issue.issueKey(), dto))
                            .toList();
                    jiraChangelogRepositoryPort.replaceForIssue(workspaceId, issueEntityId, issueChangelog);
                    changelogEvents.addAll(issueChangelog);
                    for (ExternalJiraChangelogDto changelogDto : changelogDtos) {
                        jiraRawPayloadRepositoryPort.save(
                                workspaceId,
                                connectionId,
                                "CHANGELOG",
                                changelogDto.historyId(),
                                objectMapper.valueToTree(changelogDto),
                                syncedAt
                        );
                    }
                }
            }

            JiraSprintSnapshot snapshot = jiraSprintSnapshotBuilder.build(board, sprint, issues, comments, changelogEvents);
            SyncSprintResult result = jiraSprintSnapshotBuilder.buildResult(
                    workspaceId,
                    connectionId,
                    snapshot,
                    syncedAt,
                    SprintSyncStatus.SUCCESS,
                    "Sprint sync completed synchronously. TODO: move orchestration to jobs framework."
            );
            workflowMetricsRecorder.increment("jira.sync.completed");
            workflowMetricsRecorder.recordDuration("jira.sync.duration", System.nanoTime() - startedAt, "status", "completed");
            workflowMetricsRecorder.recordCount("jira.sync.issue.count", issues.size());
            workflowMetricsRecorder.recordCount("jira.sync.comment.count", comments.size());
            workflowMetricsRecorder.recordCount("jira.sync.changelog.count", changelogEvents.size());
            LOGGER.info("jira.sync.completed workspaceId={} connectionId={} sprintId={} issues={} comments={} changelogEvents={}",
                    workspaceId, connectionId, sprintId, issues.size(), comments.size(), changelogEvents.size());
            trace.close("completed");
            return result;
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.increment("jira.sync.failed");
            workflowMetricsRecorder.recordDuration("jira.sync.duration", System.nanoTime() - startedAt, "status", "failed");
            LOGGER.error("jira.sync.failed workspaceId={} connectionId={} sprintId={}", workspaceId, connectionId, sprintId, exception);
            throw exception;
        } catch (Exception exception) {
            workflowMetricsRecorder.increment("jira.sync.failed");
            workflowMetricsRecorder.recordDuration("jira.sync.duration", System.nanoTime() - startedAt, "status", "failed");
            LOGGER.error("jira.sync.failed workspaceId={} connectionId={} sprintId={}", workspaceId, connectionId, sprintId, exception);
            throw new JiraSyncException("Sprint sync failed.", exception);
        }
    }

}
