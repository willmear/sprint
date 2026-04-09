package com.willmear.sprint.retrieval.api.request;

public record IndexSprintDocumentsRequest(
        Boolean includeComments,
        Boolean includeSprintSummary,
        Boolean forceReindex
) {
}
