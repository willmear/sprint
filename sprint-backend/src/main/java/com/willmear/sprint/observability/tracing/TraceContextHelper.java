package com.willmear.sprint.observability.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TraceContextHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceContextHelper.class);

    public Scope start(String traceName) {
        long startedAt = System.nanoTime();
        return (status) -> LOGGER.debug("trace name={} status={} durationMs={}", traceName, status, (System.nanoTime() - startedAt) / 1_000_000);
    }

    @FunctionalInterface
    public interface Scope extends AutoCloseable {
        void close(String status);

        @Override
        default void close() {
            close("completed");
        }
    }
}
