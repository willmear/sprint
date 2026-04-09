package com.willmear.sprint.api.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ConnectJiraRequest(
        UUID workspaceId,
        @NotBlank String baseUrl,
        @NotBlank String username,
        @NotBlank String apiToken
) {
}

