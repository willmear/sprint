package com.willmear.sprint.api.controller;

import com.willmear.sprint.auth.api.response.CurrentUserResponse;
import com.willmear.sprint.auth.api.AuthService;
import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.auth.security.SessionCookieService;
import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.profile.api.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final SessionCookieService sessionCookieService;

    public AuthController(
            AuthService authService,
            CurrentUserService currentUserService,
            SessionCookieService sessionCookieService
    ) {
        this.authService = authService;
        this.currentUserService = currentUserService;
        this.sessionCookieService = sessionCookieService;
    }

    @GetMapping("/jira/login")
    public ResponseEntity<Void> startJiraLogin(@RequestParam(name = "redirectTo", required = false) String redirectTo) {
        return ResponseEntity.status(302)
                .location(URI.create(authService.startJiraLogin(redirectTo)))
                .build();
    }

    @GetMapping("/jira/callback")
    public ResponseEntity<Void> completeJiraLogin(
            @RequestParam(required = false) String code,
            @RequestParam String state,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if (StringUtils.hasText(error)) {
            return ResponseEntity.status(302)
                    .location(URI.create(appendAuthStatus(authService.handleJiraLoginError(state, error, errorDescription), "error", errorDescription)))
                    .build();
        }
        if (!StringUtils.hasText(code)) {
            throw new BadRequestException("Missing OAuth authorization code.");
        }

        AuthService.LoginResult result = authService.completeJiraLogin(code, state);
        HttpHeaders headers = new HttpHeaders();
        sessionCookieService.attachSessionCookie(headers, result.session().sessionToken());
        headers.setLocation(URI.create(appendAuthStatus(result.redirectUri(), "success", null)));
        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> currentUser() {
        return ResponseEntity.ok(currentUserService.getCurrentUser()
                .map(user -> new CurrentUserResponse(true, new UserProfileResponse(
                        user.userId(),
                        user.displayName(),
                        user.email(),
                        user.avatarUrl(),
                        user.authProvider().name(),
                        user.lastLoginAt()
                )))
                .orElseGet(() -> new CurrentUserResponse(false, null)));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        authService.logout(sessionCookieService.resolveSessionToken(request));
        HttpHeaders headers = new HttpHeaders();
        sessionCookieService.attachClearedSessionCookie(headers);
        return ResponseEntity.ok().headers(headers).body(new LogoutResponse(true));
    }

    private String appendAuthStatus(String redirectUri, String status, String message) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri).queryParam("auth", status);
        if (StringUtils.hasText(message)) {
            builder.queryParam("message", message);
        }
        return builder.build(true).toUriString();
    }

    public record LogoutResponse(boolean success) {
    }
}
