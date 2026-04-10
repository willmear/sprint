import { apiClient } from "@/lib/api/client";
import type { Workspace } from "@/types/api";

export const workspaceService = {
  list: () => apiClient<Workspace[]>("/api/workspaces"),
  get: (workspaceId: string) => apiClient<Workspace>(`/api/workspaces/${workspaceId}`),
  create: (payload: { name: string; description?: string }) =>
    apiClient<Workspace>("/api/workspaces", {
      method: "POST",
      body: payload,
    }),
};
