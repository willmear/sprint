package com.willmear.sprint.artifact.api.response;

import java.util.List;

public record ArtifactListResponse(
        List<ArtifactSummaryResponse> artifacts
) {
}
