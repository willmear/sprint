package com.willmear.sprint.auth.security;

import com.willmear.sprint.auth.mapper.AppUserMapper;
import com.willmear.sprint.auth.repository.AppSessionRepository;
import com.willmear.sprint.common.exception.InvalidAuthSessionException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthSessionAuthenticationFilter extends OncePerRequestFilter {

    private final AppSessionRepository appSessionRepository;
    private final SessionTokenHasher sessionTokenHasher;
    private final SessionCookieService sessionCookieService;
    private final AppUserMapper appUserMapper;

    public AuthSessionAuthenticationFilter(
            AppSessionRepository appSessionRepository,
            SessionTokenHasher sessionTokenHasher,
            SessionCookieService sessionCookieService,
            AppUserMapper appUserMapper
    ) {
        this.appSessionRepository = appSessionRepository;
        this.sessionTokenHasher = sessionTokenHasher;
        this.sessionCookieService = sessionCookieService;
        this.appUserMapper = appUserMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String sessionToken = sessionCookieService.resolveSessionToken(request);
        if (!StringUtils.hasText(sessionToken) || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var session = appSessionRepository.findActiveBySessionTokenHash(sessionTokenHasher.hash(sessionToken), Instant.now())
                    .orElseThrow(() -> new InvalidAuthSessionException("Authentication session is invalid or expired."));
            var principal = appUserMapper.toAuthenticatedUser(session.getUser(), session.getExpiresAt());
            var authorities = principal.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, sessionToken, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidAuthSessionException exception) {
            response.addHeader(HttpHeaders.SET_COOKIE, sessionCookieService.buildClearedSessionCookie());
        }

        filterChain.doFilter(request, response);
    }
}
