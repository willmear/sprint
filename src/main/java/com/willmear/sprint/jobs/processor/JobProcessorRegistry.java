package com.willmear.sprint.jobs.processor;

import com.willmear.sprint.common.exception.UnsupportedJobTypeException;
import com.willmear.sprint.jobs.domain.JobType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JobProcessorRegistry {

    private final Map<JobType, JobProcessor> processorsByType;

    public JobProcessorRegistry(List<JobProcessor> processors) {
        this.processorsByType = new EnumMap<>(JobType.class);
        for (JobProcessor processor : processors) {
            processorsByType.put(processor.supports(), processor);
        }
    }

    public JobProcessor getProcessor(JobType jobType) {
        JobProcessor processor = processorsByType.get(jobType);
        if (processor == null) {
            throw new UnsupportedJobTypeException(jobType);
        }
        return processor;
    }
}
