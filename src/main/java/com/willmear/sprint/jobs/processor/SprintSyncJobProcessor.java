package com.willmear.sprint.jobs.processor;

import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobType;
import org.springframework.stereotype.Component;

@Component
public class SprintSyncJobProcessor implements JobProcessor {

    @Override
    public JobType supports() {
        return JobType.SYNC_SPRINT;
    }

    @Override
    public JobExecutionResult process(Job job) {
        // TODO: Route SYNC_SPRINT jobs to the sprint sync application flow.
        return JobExecutionResult.success("SYNC_SPRINT job processor placeholder completed.");
    }
}
