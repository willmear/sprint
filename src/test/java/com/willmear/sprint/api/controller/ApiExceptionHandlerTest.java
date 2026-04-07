package com.willmear.sprint.api.controller;

import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.common.exception.IntegrationException;
import com.willmear.sprint.common.exception.NotFoundException;
import com.willmear.sprint.common.model.ApiError;
import com.willmear.sprint.observability.logging.MdcKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldBuildNotFoundErrorWithCorrelationId() {
        MDC.put(MdcKeys.CORRELATION_ID, "corr-123");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/workspaces/1");

        ResponseEntity<ApiError> response = handler.handleNotFound(new NotFoundException("Workspace not found."), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("Workspace not found.");
        assertThat(response.getBody().path()).isEqualTo("/api/workspaces/1");
        assertThat(response.getBody().correlationId()).isEqualTo("corr-123");
    }

    @Test
    void shouldBuildBadRequestError() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/jobs");

        ResponseEntity<ApiError> response = handler.handleBadRequest(new BadRequestException("Invalid request."), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void shouldBuildIntegrationError() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/jira/test");

        ResponseEntity<ApiError> response = handler.handleIntegration(new IntegrationException("Downstream failed."), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTEGRATION_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Downstream failed.");
    }

    @Test
    void shouldHideUnexpectedExceptionDetails() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/oops");

        ResponseEntity<ApiError> response = handler.handleUnexpected(new IllegalStateException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred.");
    }
}
