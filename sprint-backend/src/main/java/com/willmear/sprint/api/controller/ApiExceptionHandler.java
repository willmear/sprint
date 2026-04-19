package com.willmear.sprint.api.controller;

import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.common.exception.InvalidAuthSessionException;
import com.willmear.sprint.common.exception.InsufficientDailyCreditsException;
import com.willmear.sprint.common.exception.IntegrationException;
import com.willmear.sprint.common.exception.NotFoundException;
import com.willmear.sprint.common.exception.ResourceAccessDeniedException;
import com.willmear.sprint.common.exception.UnauthenticatedException;
import com.willmear.sprint.common.exception.WorkspaceAccessDeniedException;
import com.willmear.sprint.common.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.willmear.sprint.observability.logging.MdcKeys;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException exception, HttpServletRequest request) {
        LOGGER.warn("api.request.not_found path={} message={}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException exception, HttpServletRequest request) {
        LOGGER.warn("api.request.bad_request path={} message={}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({UnauthenticatedException.class, InvalidAuthSessionException.class})
    public ResponseEntity<ApiError> handleUnauthenticated(RuntimeException exception, HttpServletRequest request) {
        LOGGER.warn("api.request.unauthenticated path={} message={}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({WorkspaceAccessDeniedException.class, ResourceAccessDeniedException.class})
    public ResponseEntity<ApiError> handleForbidden(RuntimeException exception, HttpServletRequest request) {
        LOGGER.warn("api.request.forbidden path={} message={}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InsufficientDailyCreditsException.class)
    public ResponseEntity<ApiError> handleQuotaExceeded(InsufficientDailyCreditsException exception, HttpServletRequest request) {
        LOGGER.warn("api.request.quota_exceeded path={} message={}", request.getRequestURI(), exception.getMessage());
        return build(HttpStatus.TOO_MANY_REQUESTS, "DAILY_CREDIT_LIMIT_REACHED", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ApiError> handleIntegration(IntegrationException exception, HttpServletRequest request) {
        LOGGER.error("api.request.integration_error path={} message={}", request.getRequestURI(), exception.getMessage(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTEGRATION_ERROR", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error("api.request.unexpected_error path={}", request.getRequestURI(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.", request.getRequestURI());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message, String path) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                code,
                message,
                path,
                MDC.get(MdcKeys.CORRELATION_ID)
        ));
    }
}
