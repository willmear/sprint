"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { reviewService } from "@/services/review.service";
import type { GenerateSprintReviewJobResponse, GenerateSprintReviewRequest } from "@/types/review";

export function useSprintReview(workspaceId?: string, sprintId?: string | number) {
  return useQuery({
    queryKey: ["review", workspaceId, sprintId],
    queryFn: () => reviewService.get(workspaceId!, sprintId!),
    enabled: Boolean(workspaceId && sprintId),
    retry: false,
  });
}

export function useSprintReviewContext(workspaceId?: string, sprintId?: string | number) {
  return useQuery({
    queryKey: ["review-context", workspaceId, sprintId],
    queryFn: () => reviewService.getContext(workspaceId!, sprintId!),
    enabled: Boolean(workspaceId && sprintId),
  });
}

export function useGenerateSprintReview(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: GenerateSprintReviewRequest) => reviewService.generate(workspaceId, sprintId, payload),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["review", workspaceId, sprintId] }),
        queryClient.invalidateQueries({ queryKey: ["artifacts", workspaceId, sprintId] }),
      ]);
    },
  });
}

export function useEnqueueSprintReviewJob(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: GenerateSprintReviewRequest) => reviewService.enqueue(workspaceId, sprintId, payload),
    onSuccess: async (job: GenerateSprintReviewJobResponse) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["jobs"] }),
        queryClient.invalidateQueries({ queryKey: ["job", job.jobId] }),
      ]);
    },
  });
}
