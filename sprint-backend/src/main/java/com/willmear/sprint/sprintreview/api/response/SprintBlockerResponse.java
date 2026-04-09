package com.willmear.sprint.sprintreview.api.response;

import java.util.List;

public record SprintBlockerResponse(
        String title,
        String description,
        List<String> relatedIssueKeys,
        String severity
) {
}
