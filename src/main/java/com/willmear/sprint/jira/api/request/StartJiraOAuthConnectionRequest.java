package com.willmear.sprint.jira.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StartJiraOAuthConnectionRequest(
        @NotBlank @Size(max = 512) String baseUrl,
        @NotBlank @Size(max = 1024) String redirectUri
) {
}
