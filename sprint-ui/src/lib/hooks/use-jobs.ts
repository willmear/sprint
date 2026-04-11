"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { jobService } from "@/services/job.service";
import type { Job } from "@/types/job";

export function useJobs(filters?: { workspaceId?: string; status?: string; jobType?: string }) {
  return useQuery({
    queryKey: ["jobs", filters],
    queryFn: () => jobService.list(filters),
  });
}

export function useJob(jobId?: string, options?: { enabled?: boolean; refetchInterval?: number | false }) {
  return useQuery({
    queryKey: ["job", jobId],
    queryFn: () => jobService.get(jobId!),
    enabled: Boolean(jobId) && (options?.enabled ?? true),
    refetchInterval: (query) => {
      const explicitInterval = options?.refetchInterval;
      if (!jobId || explicitInterval === false) {
        return false;
      }
      const job = query.state.data as Job | undefined;
      if (!job) {
        return explicitInterval ?? 1500;
      }
      return isTerminalJobStatus(job.status) ? false : (explicitInterval ?? 1500);
    },
  });
}

export function useRetryJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: jobService.retry,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["jobs"] });
    },
  });
}

function isTerminalJobStatus(status?: string) {
  return status === "COMPLETED" || status === "FAILED" || status === "CANCELLED";
}
