package com.willmear.sprint.jobs.repository;

import com.willmear.sprint.jobs.entity.JobEntity;
import java.time.Instant;
import java.util.List;

public interface JobRepositoryCustom {

    List<JobEntity> claimNextJobs(Instant now, int maxJobs, String workerId);
}
