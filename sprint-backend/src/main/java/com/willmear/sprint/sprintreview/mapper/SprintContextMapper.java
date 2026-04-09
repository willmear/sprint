package com.willmear.sprint.sprintreview.mapper;

import com.willmear.sprint.sprintreview.api.response.SprintContextResponse;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import org.springframework.stereotype.Component;

@Component
public class SprintContextMapper {

    private final IssueSummaryMapper issueSummaryMapper;

    public SprintContextMapper(IssueSummaryMapper issueSummaryMapper) {
        this.issueSummaryMapper = issueSummaryMapper;
    }

    public SprintContextResponse toResponse(SprintContext context) {
        return new SprintContextResponse(
                context.workspaceId(),
                context.jiraConnectionId(),
                context.externalSprintId(),
                context.sprintName(),
                context.sprintGoal(),
                context.sprintState(),
                context.sprintStartDate(),
                context.sprintEndDate(),
                context.completedIssues().stream().map(issueSummaryMapper::toResponse).toList(),
                context.inProgressIssues().stream().map(issueSummaryMapper::toResponse).toList(),
                context.carriedOverIssues().stream().map(issueSummaryMapper::toResponse).toList(),
                context.bugFixes().stream().map(issueSummaryMapper::toResponse).toList(),
                context.technicalImprovements().stream().map(issueSummaryMapper::toResponse).toList(),
                context.allIssues().stream().map(issueSummaryMapper::toResponse).toList(),
                context.notableComments(),
                context.blockers(),
                context.totalIssueCount(),
                context.totalCommentCount(),
                context.totalChangelogCount(),
                context.assembledAt()
        );
    }
}
