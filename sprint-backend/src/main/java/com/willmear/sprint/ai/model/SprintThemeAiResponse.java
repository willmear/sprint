package com.willmear.sprint.ai.model;

import java.util.List;

public record SprintThemeAiResponse(
        String name,
        String description,
        List<String> relatedIssueKeys
) {
}
