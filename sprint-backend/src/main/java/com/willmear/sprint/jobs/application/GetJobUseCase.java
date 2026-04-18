package com.willmear.sprint.jobs.application;

import com.willmear.sprint.common.exception.JobNotFoundException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetJobUseCase {

    private final JobRepositoryAdapter jobRepositoryAdapter;
    private final WorkspaceService workspaceService;

    public GetJobUseCase(JobRepositoryAdapter jobRepositoryAdapter, WorkspaceService workspaceService) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
        this.workspaceService = workspaceService;
    }

    public Job get(UUID jobId) {
        Job job = jobRepositoryAdapter.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
        if (job.workspaceId() != null) {
            workspaceService.getWorkspace(job.workspaceId());
        }
        return job;
    }
}
