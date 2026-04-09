package com.willmear.sprint.api.controller;

import com.willmear.sprint.api.request.CreateJobRequest;
import com.willmear.sprint.api.request.RetryJobRequest;
import com.willmear.sprint.api.response.CreateJobResponse;
import com.willmear.sprint.api.response.JobResponse;
import com.willmear.sprint.api.response.JobSummaryResponse;
import com.willmear.sprint.jobs.api.JobService;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.mapper.JobResponseMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final JobResponseMapper jobResponseMapper;

    public JobController(JobService jobService, JobResponseMapper jobResponseMapper) {
        this.jobService = jobService;
        this.jobResponseMapper = jobResponseMapper;
    }

    @PostMapping
    public ResponseEntity<CreateJobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponseMapper.toCreateResponse(
                jobService.createJob(request.workspaceId(), request.jobType(), request.payload(), request.maxAttempts(), request.availableAt())
        ));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobResponseMapper.toResponse(jobService.getJob(jobId)));
    }

    @GetMapping
    public ResponseEntity<List<JobSummaryResponse>> listJobs(
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobType jobType
    ) {
        List<JobSummaryResponse> responses = jobService.listJobs(new JobFilter(workspaceId, status, jobType)).stream()
                .map(jobResponseMapper::toSummaryResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{jobId}/retry")
    public ResponseEntity<JobResponse> retryJob(@PathVariable UUID jobId, @RequestBody(required = false) RetryJobRequest request) {
        return ResponseEntity.ok(jobResponseMapper.toResponse(jobService.retryJob(jobId)));
    }
}
