package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import java.util.UUID;

public interface JiraOAuthClientPort {

    String buildAuthorizationUrl(UUID workspaceId, UUID connectionId, String state, String redirectUri);

    JiraOAuthTokenResponse exchangeCodeForTokens(String code, String redirectUri);
}
