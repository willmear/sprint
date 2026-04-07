package com.willmear.sprint.jira.domain.service;

import com.willmear.sprint.jira.domain.model.JiraBoard;
import com.willmear.sprint.jira.domain.model.JiraChangelogEvent;
import com.willmear.sprint.jira.domain.model.JiraComment;
import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraChangelogDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraCommentDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueFieldsDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JiraIssueNormaliser {

    public JiraBoard normaliseBoard(UUID workspaceId, ExternalJiraBoardDto dto) {
        return new JiraBoard(dto.id(), workspaceId, dto.name(), dto.type(), dto.projectKey());
    }

    public JiraSprint normaliseSprint(UUID workspaceId, UUID jiraConnectionId, ExternalJiraSprintDto dto, Instant syncedAt) {
        return new JiraSprint(
                dto.id(),
                workspaceId,
                jiraConnectionId,
                dto.boardId(),
                dto.name(),
                dto.goal(),
                dto.state(),
                dto.startDate(),
                dto.endDate(),
                dto.completeDate(),
                syncedAt
        );
    }

    public JiraIssue normaliseIssue(UUID workspaceId, UUID jiraConnectionId, Long externalSprintId, ExternalJiraIssueDto dto) {
        ExternalJiraIssueFieldsDto fields = dto.fields();
        return new JiraIssue(
                workspaceId,
                jiraConnectionId,
                externalSprintId,
                dto.key(),
                dto.id(),
                fields.summary(),
                fields.description(),
                fields.issueType(),
                fields.status(),
                fields.priority(),
                fields.assignee() != null ? fields.assignee().displayName() : null,
                fields.reporter() != null ? fields.reporter().displayName() : null,
                fields.storyPoints(),
                fields.created(),
                fields.updated()
        );
    }

    public JiraComment normaliseComment(UUID workspaceId, String issueKey, ExternalJiraCommentDto dto) {
        return new JiraComment(
                workspaceId,
                dto.id(),
                issueKey,
                dto.author() != null ? dto.author().displayName() : null,
                dto.body(),
                dto.created(),
                dto.updated()
        );
    }

    public JiraChangelogEvent normaliseChangelog(UUID workspaceId, String issueKey, ExternalJiraChangelogDto dto) {
        return new JiraChangelogEvent(
                workspaceId,
                dto.historyId(),
                issueKey,
                dto.fieldName(),
                dto.fromValue(),
                dto.toValue(),
                dto.changedAt(),
                dto.author() != null ? dto.author().displayName() : null
        );
    }
}
