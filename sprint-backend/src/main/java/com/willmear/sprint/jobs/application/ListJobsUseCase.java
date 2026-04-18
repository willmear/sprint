package com.willmear.sprint.jobs.application;

import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.willmear.sprint.workspace.api.WorkspaceService;

@Service
public class ListJobsUseCase {

    private final JobRepositoryAdapter jobRepositoryAdapter;
    private final WorkspaceService workspaceService;

    public ListJobsUseCase(JobRepositoryAdapter jobRepositoryAdapter, WorkspaceService workspaceService) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
        this.workspaceService = workspaceService;
    }

    public List<Job> list(JobFilter filter) {
        if (filter.workspaceId() != null) {
            workspaceService.getWorkspace(filter.workspaceId());
            return jobRepositoryAdapter.findByFilter(filter);
        }
        Set<java.util.UUID> ownedWorkspaceIds = workspaceService.listWorkspaces().stream()
                .map(com.willmear.sprint.workspace.domain.model.Workspace::id)
                .collect(java.util.stream.Collectors.toSet());
        return jobRepositoryAdapter.findByFilter(filter).stream()
                .filter(job -> job.workspaceId() != null && ownedWorkspaceIds.contains(job.workspaceId()))
                .toList();
    }
}
