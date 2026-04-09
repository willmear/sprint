package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import java.util.List;
import java.util.UUID;

public interface JiraOAuthClientPort {

    String buildAuthorizationUrl(UUID workspaceId, UUID connectionId, String state, String redirectUri);

    JiraOAuthTokenResponse exchangeCodeForTokens(String code, String redirectUri);

    JiraOAuthTokenResponse refreshAccessToken(String refreshToken);

    List<JiraAccessibleResource> getAccessibleResources(String accessToken);
}
