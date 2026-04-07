package com.willmear.sprint.observability.logging;

import com.willmear.sprint.config.ObservabilityProperties;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldReuseIncomingCorrelationIdAndCleanUpMdc() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter(new ObservabilityProperties(true, "X-Correlation-Id", true, true, true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/jobs");
        request.addHeader("X-Correlation-Id", "corr-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("corr-123");
        assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isNull();
    }

    @Test
    void shouldFallbackToRequestIdHeaderWhenPrimaryHeaderMissing() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter(new ObservabilityProperties(true, "X-Correlation-Id", false, false, true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/jobs");
        request.addHeader("X-Request-Id", "request-456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("request-456");
    }

    @Test
    void shouldGenerateCorrelationIdWhenNoHeadersPresent() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter(new ObservabilityProperties(true, "X-Correlation-Id", false, false, true));
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/workspaces");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("X-Correlation-Id")).isNotBlank();
        assertThatCodeIsUuid(response.getHeader("X-Correlation-Id"));
    }

    private void assertThatCodeIsUuid(String value) {
        assertThat(UUID.fromString(value)).isNotNull();
    }
}
