"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { sprintService } from "@/services/sprint.service";

export function useSprints(workspaceId?: string) {
  return useQuery({
    queryKey: ["sprints", workspaceId],
    queryFn: () => sprintService.list(workspaceId!),
    enabled: Boolean(workspaceId),
  });
}

export function useAvailableJiraSprints(workspaceId?: string, connectionId?: string) {
  return useQuery({
    queryKey: ["available-jira-sprints", workspaceId, connectionId],
    queryFn: () => sprintService.listAvailable(workspaceId!, connectionId!),
    enabled: Boolean(workspaceId && connectionId),
  });
}

export function useSprint(workspaceId?: string, sprintId?: string | number) {
  return useQuery({
    queryKey: ["sprint", workspaceId, sprintId],
    queryFn: () => sprintService.get(workspaceId!, sprintId!),
    enabled: Boolean(workspaceId && sprintId),
  });
}

export function useSprintIssues(workspaceId?: string, sprintId?: string | number) {
  return useQuery({
    queryKey: ["sprint-issues", workspaceId, sprintId],
    queryFn: () => sprintService.getIssues(workspaceId!, sprintId!),
    enabled: Boolean(workspaceId && sprintId),
  });
}

export function useSyncSprint(workspaceId: string, connectionId: string | undefined) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sprintId: string | number) => sprintService.sync(workspaceId, connectionId!, sprintId),
    onSuccess: async (_, sprintId) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["sprints", workspaceId] }),
        queryClient.invalidateQueries({ queryKey: ["sprint", workspaceId, sprintId] }),
        queryClient.invalidateQueries({ queryKey: ["sprint-issues", workspaceId, sprintId] }),
        queryClient.invalidateQueries({ queryKey: ["review", workspaceId, sprintId] }),
        queryClient.invalidateQueries({ queryKey: ["review-context", workspaceId, sprintId] }),
      ]);
    },
  });
}
