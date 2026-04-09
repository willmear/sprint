package com.willmear.sprint.jobs.support;

import com.willmear.sprint.config.JobsProperties;
import org.springframework.stereotype.Component;

@Component
public class JobWorkerIdentityProvider {

    private final JobsProperties jobsProperties;

    public JobWorkerIdentityProvider(JobsProperties jobsProperties) {
        this.jobsProperties = jobsProperties;
    }

    public String workerId() {
        return jobsProperties.workerId();
    }
}
