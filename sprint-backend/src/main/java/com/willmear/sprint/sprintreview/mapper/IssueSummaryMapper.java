package com.willmear.sprint.sprintreview.mapper;

import com.willmear.sprint.sprintreview.api.response.IssueSummaryResponse;
import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import org.springframework.stereotype.Component;

@Component
public class IssueSummaryMapper {

    private final IssueCommentSummaryMapper issueCommentSummaryMapper;

    public IssueSummaryMapper(IssueCommentSummaryMapper issueCommentSummaryMapper) {
        this.issueCommentSummaryMapper = issueCommentSummaryMapper;
    }

    public IssueSummaryResponse toResponse(IssueSummary issueSummary) {
        return new IssueSummaryResponse(
                issueSummary.issueKey(),
                issueSummary.summary(),
                issueSummary.description(),
                issueSummary.issueType(),
                issueSummary.status(),
                issueSummary.priority(),
                issueSummary.assigneeDisplayName(),
                issueSummary.storyPoints(),
                issueSummary.bugFix(),
                issueSummary.technicalWork(),
                issueSummary.comments().stream().map(issueCommentSummaryMapper::toResponse).toList()
        );
    }
}
