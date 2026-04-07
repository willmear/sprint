package com.willmear.sprint.observability.logging;

import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingContextHelperTest {

    private final LoggingContextHelper helper = new LoggingContextHelper();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldRestorePreviousValueAfterScopedPut() throws Exception {
        MDC.put(MdcKeys.WORKSPACE_ID, "old-workspace");

        try (LoggingContextHelper.Scope ignored = helper.put(MdcKeys.WORKSPACE_ID, "new-workspace")) {
            assertThat(MDC.get(MdcKeys.WORKSPACE_ID)).isEqualTo("new-workspace");
        }

        assertThat(MDC.get(MdcKeys.WORKSPACE_ID)).isEqualTo("old-workspace");
    }

    @Test
    void shouldApplyMultipleValuesAndRestorePriorState() throws Exception {
        MDC.put(MdcKeys.CORRELATION_ID, "corr-1");

        try (LoggingContextHelper.Scope ignored = helper.putAll(Map.of(
                MdcKeys.CORRELATION_ID, "corr-2",
                MdcKeys.JOB_ID, "job-1"
        ))) {
            assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isEqualTo("corr-2");
            assertThat(MDC.get(MdcKeys.JOB_ID)).isEqualTo("job-1");
        }

        assertThat(MDC.get(MdcKeys.CORRELATION_ID)).isEqualTo("corr-1");
        assertThat(MDC.get(MdcKeys.JOB_ID)).isNull();
    }

    @Test
    void shouldIgnoreNullValues() throws Exception {
        try (LoggingContextHelper.Scope ignored = helper.put("ignored", null)) {
            assertThat(MDC.get("ignored")).isNull();
        }

        assertThat(MDC.get("ignored")).isNull();
    }
}
