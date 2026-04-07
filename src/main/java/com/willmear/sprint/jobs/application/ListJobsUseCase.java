package com.willmear.sprint.jobs.application;

import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListJobsUseCase {

    private final JobRepositoryAdapter jobRepositoryAdapter;

    public ListJobsUseCase(JobRepositoryAdapter jobRepositoryAdapter) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
    }

    public List<Job> list(JobFilter filter) {
        return jobRepositoryAdapter.findByFilter(filter);
    }
}
