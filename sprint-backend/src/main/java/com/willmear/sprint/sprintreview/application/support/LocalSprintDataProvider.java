package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.common.exception.SprintDataNotFoundException;
import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.infrastructure.entity.JiraChangelogEventEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.sprintreview.domain.port.SprintDataProviderPort;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LocalSprintDataProvider implements SprintDataProviderPort {

    private final WorkspaceService workspaceService;
    private final JiraSprintRepository jiraSprintRepository;
    private final JiraIssueRepository jiraIssueRepository;
    private final JiraCommentRepository jiraCommentRepository;
    private final JiraChangelogEventRepository jiraChangelogEventRepository;

    public LocalSprintDataProvider(
            WorkspaceService workspaceService,
            JiraSprintRepository jiraSprintRepository,
            JiraIssueRepository jiraIssueRepository,
            JiraCommentRepository jiraCommentRepository,
            JiraChangelogEventRepository jiraChangelogEventRepository
    ) {
        this.workspaceService = workspaceService;
        this.jiraSprintRepository = jiraSprintRepository;
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraCommentRepository = jiraCommentRepository;
        this.jiraChangelogEventRepository = jiraChangelogEventRepository;
    }

    @Override
    public SprintDataBundle getSprintData(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog) {
        workspaceService.getWorkspace(workspaceId);
        JiraSprintEntity sprintEntity = jiraSprintRepository.findByWorkspace_IdAndExternalSprintId(workspaceId, externalSprintId)
                .orElseThrow(() -> new SprintDataNotFoundException(workspaceId, externalSprintId));

        List<JiraIssueEntity> issueEntities = jiraIssueRepository.findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(workspaceId, externalSprintId);
        List<JiraIssue> issues = issueEntities.stream().map(this::toIssue).toList();

        List<JiraComment> comments = new ArrayList<>();
        List<JiraChangelogEvent> changelogEvents = new ArrayList<>();
        for (JiraIssueEntity issueEntity : issueEntities) {
            if (includeComments) {
                comments.addAll(jiraCommentRepository.findByJiraIssue_IdOrderByCreatedAtExternalAsc(issueEntity.getId()).stream()
                        .map(this::toComment)
                        .toList());
            }
            if (includeChangelog) {
                changelogEvents.addAll(jiraChangelogEventRepository.findByJiraIssue_IdOrderByChangedAtAsc(issueEntity.getId()).stream()
                        .map(this::toChangelogEvent)
                        .toList());
            }
        }

        return new SprintDataBundle(
                workspaceId,
                sprintEntity.getJiraConnection().getId(),
                toSprint(sprintEntity),
                issues,
                comments,
                changelogEvents
        );
    }

    private JiraSprint toSprint(JiraSprintEntity entity) {
        return new JiraSprint(
                entity.getExternalSprintId(),
                entity.getWorkspace().getId(),
                entity.getJiraConnection().getId(),
                entity.getExternalBoardId(),
                entity.getName(),
                entity.getGoal(),
                entity.getState(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCompleteDate(),
                entity.getSyncedAt()
        );
    }

    private JiraIssue toIssue(JiraIssueEntity entity) {
        return new JiraIssue(
                entity.getWorkspace().getId(),
                entity.getJiraConnection().getId(),
                entity.getExternalSprintId(),
                entity.getIssueKey(),
                entity.getExternalIssueId(),
                entity.getSummary(),
                entity.getDescription(),
                entity.getIssueType(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAssigneeDisplayName(),
                entity.getReporterDisplayName(),
                entity.getStoryPoints(),
                entity.getCreatedAtExternal(),
                entity.getUpdatedAtExternal()
        );
    }

    private JiraComment toComment(JiraCommentEntity entity) {
        return new JiraComment(
                entity.getWorkspace().getId(),
                entity.getExternalCommentId(),
                entity.getIssueKey(),
                entity.getAuthorDisplayName(),
                entity.getBody(),
                entity.getCreatedAtExternal(),
                entity.getUpdatedAtExternal()
        );
    }

    private JiraChangelogEvent toChangelogEvent(JiraChangelogEventEntity entity) {
        return new JiraChangelogEvent(
                entity.getWorkspace().getId(),
                entity.getExternalHistoryId(),
                entity.getIssueKey(),
                entity.getFieldName(),
                entity.getFromValue(),
                entity.getToValue(),
                entity.getChangedAt(),
                entity.getAuthorDisplayName()
        );
    }
}
