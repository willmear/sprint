"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { jiraService } from "@/services/jira.service";

export function useJiraConnections(workspaceId?: string) {
  return useQuery({
    queryKey: ["jira-connections", workspaceId],
    queryFn: () => jiraService.listConnections(workspaceId!),
    enabled: Boolean(workspaceId),
  });
}

export function useStartOAuth(workspaceId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: { baseUrl: string }) => jiraService.startOAuth(workspaceId, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["jira-connections", workspaceId] });
    },
  });
}

export function useTestConnection(workspaceId: string) {
  return useMutation({
    mutationFn: (connectionId: string) => jiraService.testConnection(workspaceId, connectionId),
  });
}

export function useDisconnectConnection(workspaceId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (connectionId: string) => jiraService.disconnect(workspaceId, connectionId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["jira-connections", workspaceId] });
    },
  });
}

export function useRemoveConnection(workspaceId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (connectionId: string) => jiraService.remove(workspaceId, connectionId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["jira-connections", workspaceId] });
    },
  });
}
