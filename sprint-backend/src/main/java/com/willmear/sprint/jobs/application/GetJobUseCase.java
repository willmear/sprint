package com.willmear.sprint.jobs.application;

import com.willmear.sprint.common.exception.JobNotFoundException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetJobUseCase {

    private final JobRepositoryAdapter jobRepositoryAdapter;

    public GetJobUseCase(JobRepositoryAdapter jobRepositoryAdapter) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
    }

    public Job get(UUID jobId) {
        return jobRepositoryAdapter.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
    }
}
