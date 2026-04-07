package com.willmear.sprint.sprintreview.api.response;

import java.util.List;

public record SprintThemeResponse(
        String name,
        String description,
        List<String> relatedIssueKeys
) {
}
