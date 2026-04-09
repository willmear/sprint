package com.willmear.sprint.ai.model;

import java.util.List;

public record SprintBlockerAiResponse(
        String title,
        String description,
        List<String> relatedIssueKeys,
        String severity
) {
}
