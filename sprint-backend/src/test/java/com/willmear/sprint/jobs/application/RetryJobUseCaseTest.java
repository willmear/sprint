package com.willmear.sprint.jobs.application;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.common.exception.JobRetryNotAllowedException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetryJobUseCaseTest {

    private final GetJobUseCase getJobUseCase = mock(GetJobUseCase.class);
    private final JobRepositoryAdapter jobRepositoryAdapter = mock(JobRepositoryAdapter.class);
    private final RetryJobUseCase useCase = new RetryJobUseCase(getJobUseCase, jobRepositoryAdapter);

    @Test
    void shouldResetFailedJobForRetry() {
        UUID jobId = UUID.randomUUID();
        when(getJobUseCase.get(jobId)).thenReturn(job(jobId, JobStatus.FAILED));
        when(jobRepositoryAdapter.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job retried = useCase.retry(jobId);

        assertThat(retried.status()).isEqualTo(JobStatus.PENDING);
        assertThat(retried.failedAt()).isNull();
        assertThat(retried.errorCode()).isNull();
        assertThat(retried.availableAt()).isNotNull();
        verify(jobRepositoryAdapter).save(any(Job.class));
    }

    @Test
    void shouldRejectRetryForNonFailedJob() {
        UUID jobId = UUID.randomUUID();
        when(getJobUseCase.get(jobId)).thenReturn(job(jobId, JobStatus.COMPLETED));

        assertThatThrownBy(() -> useCase.retry(jobId))
                .isInstanceOf(JobRetryNotAllowedException.class);
    }

    private Job job(UUID jobId, JobStatus status) {
        Instant now = Instant.now();
        return new Job(jobId, UUID.randomUUID(), JobType.SYNC_SPRINT, status, "default", JsonNodeFactory.instance.objectNode(), 1, 3, now, now, "worker", now, now, now, "err", "E1", now.minusSeconds(10), now);
    }
}
