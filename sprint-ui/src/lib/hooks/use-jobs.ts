"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { jobService } from "@/services/job.service";

export function useJobs(filters?: { workspaceId?: string; status?: string; jobType?: string }) {
  return useQuery({
    queryKey: ["jobs", filters],
    queryFn: () => jobService.list(filters),
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
