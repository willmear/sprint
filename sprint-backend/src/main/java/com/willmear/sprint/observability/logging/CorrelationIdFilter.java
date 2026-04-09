package com.willmear.sprint.observability.logging;

import com.willmear.sprint.config.ObservabilityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private final ObservabilityProperties observabilityProperties;

    public CorrelationIdFilter(ObservabilityProperties observabilityProperties) {
        this.observabilityProperties = observabilityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);
        long startedAt = System.nanoTime();
        response.setHeader(observabilityProperties.correlationHeaderName(), correlationId);

        try (LoggingContextHelper.Scope ignored = putCorrelationId(correlationId)) {
            if (observabilityProperties.httpRequestLoggingEnabled() && observabilityProperties.logRequestStart()) {
                LOGGER.info("http.request.start method={} path={}", request.getMethod(), request.getRequestURI());
            }
            filterChain.doFilter(request, response);
        } finally {
            if (observabilityProperties.httpRequestLoggingEnabled() && observabilityProperties.logRequestEnd()) {
                long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
                LOGGER.info(
                        "http.request.end method={} path={} status={} durationMs={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        durationMs
                );
            }
        }
    }

    private LoggingContextHelper.Scope putCorrelationId(String correlationId) {
        String previous = org.slf4j.MDC.get(MdcKeys.CORRELATION_ID);
        org.slf4j.MDC.put(MdcKeys.CORRELATION_ID, correlationId);
        return () -> {
            if (previous == null) {
                org.slf4j.MDC.remove(MdcKeys.CORRELATION_ID);
            } else {
                org.slf4j.MDC.put(MdcKeys.CORRELATION_ID, previous);
            }
        };
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(observabilityProperties.correlationHeaderName()))
                .filter(value -> !value.isBlank())
                .or(() -> Optional.ofNullable(request.getHeader("X-Request-Id")).filter(value -> !value.isBlank()))
                .orElseGet(() -> UUID.randomUUID().toString());
    }
}
