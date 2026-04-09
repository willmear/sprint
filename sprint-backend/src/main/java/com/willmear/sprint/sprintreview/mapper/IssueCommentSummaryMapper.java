package com.willmear.sprint.sprintreview.mapper;

import com.willmear.sprint.sprintreview.api.response.IssueCommentSummaryResponse;
import com.willmear.sprint.sprintreview.domain.model.IssueCommentSummary;
import org.springframework.stereotype.Component;

@Component
public class IssueCommentSummaryMapper {

    public IssueCommentSummaryResponse toResponse(IssueCommentSummary issueCommentSummary) {
        return new IssueCommentSummaryResponse(
                issueCommentSummary.authorDisplayName(),
                issueCommentSummary.body(),
                issueCommentSummary.createdAtExternal()
        );
    }
}
