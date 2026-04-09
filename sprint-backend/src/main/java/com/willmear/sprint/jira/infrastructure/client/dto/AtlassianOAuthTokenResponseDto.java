package com.willmear.sprint.jira.infrastructure.client.dto;

public record AtlassianOAuthTokenResponseDto(
        String access_token,
        String refresh_token,
        Long expires_in,
        String scope,
        String token_type
) {
}
