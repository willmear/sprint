package com.willmear.sprint.sprintreview.domain.model;

import java.util.List;

public record SprintHighlight(
        String title,
        String description,
        List<String> relatedIssueKeys,
        String category
) {
}
