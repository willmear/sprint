package com.willmear.sprint.auth.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SessionCookieService {

    private final AuthProperties authProperties;

    public SessionCookieService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String buildSessionCookie(String sessionToken) {
        return ResponseCookie.from(authProperties.cookieName(), sessionToken)
                .httpOnly(true)
                .secure(authProperties.cookieSecure())
                .sameSite(authProperties.cookieSameSite())
                .path("/")
                .maxAge(authProperties.sessionTtl())
                .build()
                .toString();
    }

    public String buildClearedSessionCookie() {
        return ResponseCookie.from(authProperties.cookieName(), "")
                .httpOnly(true)
                .secure(authProperties.cookieSecure())
                .sameSite(authProperties.cookieSameSite())
                .path("/")
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }

    public String resolveSessionToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (authProperties.cookieName().equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void attachSessionCookie(HttpHeaders headers, String sessionToken) {
        headers.add(HttpHeaders.SET_COOKIE, buildSessionCookie(sessionToken));
    }

    public void attachClearedSessionCookie(HttpHeaders headers) {
        headers.add(HttpHeaders.SET_COOKIE, buildClearedSessionCookie());
    }
}
