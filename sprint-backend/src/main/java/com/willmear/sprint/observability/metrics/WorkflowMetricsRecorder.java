package com.willmear.sprint.observability.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMetricsRecorder {

    public static final String PREFIX = "sprint.app";

    private final MeterRegistry meterRegistry;

    public WorkflowMetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void increment(String metric, String... tags) {
        Counter.builder(PREFIX + "." + metric)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    public void recordDuration(String metric, long durationNanos, String... tags) {
        Timer.builder(PREFIX + "." + metric)
                .tags(tags)
                .register(meterRegistry)
                .record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordCount(String metric, int amount, String... tags) {
        DistributionSummary.builder(PREFIX + "." + metric)
                .tags(tags)
                .register(meterRegistry)
                .record(amount);
    }
}
