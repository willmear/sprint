import { apiClient } from "@/lib/api/client";
import type { AvailableJiraSprint, Issue, Sprint, SyncSprintResponse } from "@/types/sprint";

export const sprintService = {
  listAvailable: (workspaceId: string, connectionId: string) =>
    apiClient<AvailableJiraSprint[]>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}/available-sprints`),
  list: (workspaceId: string) => apiClient<Sprint[]>(`/api/workspaces/${workspaceId}/sprints`),
  get: (workspaceId: string, sprintId: string | number) =>
    apiClient<Sprint>(`/api/workspaces/${workspaceId}/sprints/${sprintId}`),
  getIssues: (workspaceId: string, sprintId: string | number) =>
    apiClient<Issue[]>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/issues`),
  sync: (
    workspaceId: string,
    connectionId: string,
    sprintId: string | number,
    payload: { includeComments?: boolean; includeChangelog?: boolean } = {}
  ) =>
    apiClient<SyncSprintResponse>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}/sprints/${sprintId}/sync`, {
      method: "POST",
      body: {
        boardId: null,
        includeComments: payload.includeComments ?? true,
        includeChangelog: payload.includeChangelog ?? true,
      },
    }),
};
