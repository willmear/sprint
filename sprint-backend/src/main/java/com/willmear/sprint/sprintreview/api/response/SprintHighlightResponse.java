package com.willmear.sprint.sprintreview.api.response;

import java.util.List;

public record SprintHighlightResponse(
        String title,
        String description,
        List<String> relatedIssueKeys,
        String category
) {
}
