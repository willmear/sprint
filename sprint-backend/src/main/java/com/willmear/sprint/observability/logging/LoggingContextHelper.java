package com.willmear.sprint.observability.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LoggingContextHelper {

    public Scope put(String key, Object value) {
        if (value == null) {
            return () -> { };
        }
        String previous = MDC.get(key);
        MDC.put(key, String.valueOf(value));
        return () -> restore(key, previous);
    }

    public Scope putAll(Map<String, ?> values) {
        Map<String, String> previousValues = new LinkedHashMap<>();
        values.forEach((key, value) -> {
            previousValues.put(key, MDC.get(key));
            if (value != null) {
                MDC.put(key, String.valueOf(value));
            }
        });
        return () -> previousValues.forEach(this::restore);
    }

    private void restore(String key, String previous) {
        if (previous == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, previous);
        }
    }

    @FunctionalInterface
    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }
}
