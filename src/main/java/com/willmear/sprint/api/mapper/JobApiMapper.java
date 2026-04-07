package com.willmear.sprint.api.mapper;

import com.willmear.sprint.api.response.JobStatusResponse;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.mapper.JobResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class JobApiMapper {

    private final JobResponseMapper jobResponseMapper;

    public JobApiMapper(JobResponseMapper jobResponseMapper) {
        this.jobResponseMapper = jobResponseMapper;
    }

    public JobStatusResponse toResponse(Job job) {
        return jobResponseMapper.toStatusResponse(job);
    }
}
