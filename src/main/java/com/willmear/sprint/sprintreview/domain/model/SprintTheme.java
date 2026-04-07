package com.willmear.sprint.sprintreview.domain.model;

import java.util.List;

public record SprintTheme(
        String name,
        String description,
        List<String> relatedIssueKeys
) {
}
