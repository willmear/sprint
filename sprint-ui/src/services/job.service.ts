import { apiClient } from "@/lib/api/client";
import type { CreateJobResponse, Job, JobSummary } from "@/types/job";

export const jobService = {
  list: (params?: { workspaceId?: string; status?: string; jobType?: string }) => {
    const search = new URLSearchParams();
    if (params?.workspaceId) search.set("workspaceId", params.workspaceId);
    if (params?.status) search.set("status", params.status);
    if (params?.jobType) search.set("jobType", params.jobType);
    const suffix = search.toString() ? `?${search.toString()}` : "";
    return apiClient<JobSummary[]>(`/api/jobs${suffix}`);
  },
  get: (jobId: string) => apiClient<Job>(`/api/jobs/${jobId}`),
  create: (payload: {
    workspaceId: string;
    jobType: string;
    payload: Record<string, unknown>;
    maxAttempts?: number;
    availableAt?: string | null;
  }) =>
    apiClient<CreateJobResponse>("/api/jobs", {
      method: "POST",
      body: payload,
    }),
  retry: (jobId: string) =>
    apiClient<Job>(`/api/jobs/${jobId}/retry`, {
      method: "POST",
      body: {},
    }),
};
