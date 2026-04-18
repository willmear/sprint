package com.willmear.sprint.auth.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        String cookieName,
        Duration sessionTtl,
        Duration loginStateTtl,
        String jiraLoginRedirectUri,
        String defaultPostLoginPath,
        boolean cookieSecure,
        String cookieSameSite
) {
}
