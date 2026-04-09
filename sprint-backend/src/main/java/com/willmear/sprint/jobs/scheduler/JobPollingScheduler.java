package com.willmear.sprint.jobs.scheduler;

import com.willmear.sprint.config.JobsProperties;
import com.willmear.sprint.jobs.application.ClaimNextJobUseCase;
import com.willmear.sprint.jobs.application.ProcessJobUseCase;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobPollingScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobPollingScheduler.class);

    private final ClaimNextJobUseCase claimNextJobUseCase;
    private final ProcessJobUseCase processJobUseCase;
    private final JobsProperties jobsProperties;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;

    public JobPollingScheduler(
            ClaimNextJobUseCase claimNextJobUseCase,
            ProcessJobUseCase processJobUseCase,
            JobsProperties jobsProperties,
            WorkflowMetricsRecorder workflowMetricsRecorder
    ) {
        this.claimNextJobUseCase = claimNextJobUseCase;
        this.processJobUseCase = processJobUseCase;
        this.jobsProperties = jobsProperties;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
    }

    @Scheduled(fixedDelayString = "${app.jobs.poll-interval:5000ms}")
    public void poll() {
        if (!jobsProperties.enabled()) {
            return;
        }

        List<Job> claimedJobs = claimNextJobUseCase.claimNextJobs(jobsProperties.maxJobsPerPoll());
        if (!claimedJobs.isEmpty()) {
            LOGGER.info("job.poll.claimed workerId={} claimedCount={}", jobsProperties.workerId(), claimedJobs.size());
            workflowMetricsRecorder.recordCount("jobs.claimed.count", claimedJobs.size(), "workerId", jobsProperties.workerId());
        }
        for (Job job : claimedJobs) {
            try {
                processJobUseCase.process(job);
            } catch (RuntimeException exception) {
                LOGGER.error("Job processing failed for job {}", job.id(), exception);
            }
        }
    }
}
