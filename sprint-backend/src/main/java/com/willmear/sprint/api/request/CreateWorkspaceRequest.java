package com.willmear.sprint.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description
) {
}
