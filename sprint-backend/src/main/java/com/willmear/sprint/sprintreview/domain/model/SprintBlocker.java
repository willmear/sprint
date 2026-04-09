package com.willmear.sprint.sprintreview.domain.model;

import java.util.List;

public record SprintBlocker(
        String title,
        String description,
        List<String> relatedIssueKeys,
        String severity
) {
}
