package com.willmear.sprint.jobs.application;

import com.willmear.sprint.common.exception.JobClaimException;
import com.willmear.sprint.config.JobsProperties;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.jobs.support.JobWorkerIdentityProvider;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClaimNextJobUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimNextJobUseCase.class);

    private final JobRepositoryAdapter jobRepositoryAdapter;
    private final JobsProperties jobsProperties;
    private final JobWorkerIdentityProvider jobWorkerIdentityProvider;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;

    public ClaimNextJobUseCase(
            JobRepositoryAdapter jobRepositoryAdapter,
            JobsProperties jobsProperties,
            JobWorkerIdentityProvider jobWorkerIdentityProvider,
            WorkflowMetricsRecorder workflowMetricsRecorder
    ) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
        this.jobsProperties = jobsProperties;
        this.jobWorkerIdentityProvider = jobWorkerIdentityProvider;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
    }

    public List<Job> claimNextJobs(int maxJobs) {
        try {
            List<Job> claimed = jobRepositoryAdapter.claimNextJobs(Instant.now(), maxJobs, jobWorkerIdentityProvider.workerId());
            if (!claimed.isEmpty()) {
                LOGGER.info("job.claim.completed workerId={} claimedCount={} queue={}",
                        jobWorkerIdentityProvider.workerId(), claimed.size(), jobsProperties.defaultQueueName());
                workflowMetricsRecorder.recordCount("jobs.claimed.count", claimed.size(), "workerId", jobWorkerIdentityProvider.workerId());
            }
            return claimed;
        } catch (RuntimeException exception) {
            LOGGER.error("job.claim.failed workerId={}", jobWorkerIdentityProvider.workerId(), exception);
            throw new JobClaimException("Failed to claim pending jobs.", exception);
        }
    }
}
