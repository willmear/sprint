package com.willmear.sprint.jobs.processor;

import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobType;

public interface JobProcessor {

    JobType supports();

    JobExecutionResult process(Job job);
}
