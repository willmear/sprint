"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { workspaceService } from "@/services/workspace.service";

export function useWorkspaces() {
  return useQuery({
    queryKey: ["workspaces"],
    queryFn: workspaceService.list,
  });
}

export function useCreateWorkspace() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: workspaceService.create,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["workspaces"] });
    },
  });
}
