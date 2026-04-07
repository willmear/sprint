package com.willmear.sprint.retrieval.application;

import java.util.UUID;

public record IndexSprintDocumentsJobPayload(
        UUID workspaceId,
        Long externalSprintId,
        boolean includeComments,
        boolean includeSprintSummary,
        boolean forceReindex
) {
}
