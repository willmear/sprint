import { apiClient } from "@/lib/api/client";
import type { Artifact, ArtifactListResponse, LatestArtifactResponse } from "@/types/api";

export const artifactService = {
  getById: (artifactId: string) => apiClient<Artifact>(`/api/artifacts/${artifactId}`),
  listByWorkspace: (workspaceId: string) => apiClient<ArtifactListResponse>(`/api/workspaces/${workspaceId}/artifacts`),
  listBySprint: (workspaceId: string, sprintId: string | number) =>
    apiClient<ArtifactListResponse>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/artifacts`),
  getLatestByWorkspace: (workspaceId: string, artifactType: string, referenceType?: string, referenceId?: string) => {
    const search = new URLSearchParams({ artifactType });
    if (referenceType) search.set("referenceType", referenceType);
    if (referenceId) search.set("referenceId", referenceId);
    return apiClient<LatestArtifactResponse>(`/api/workspaces/${workspaceId}/artifacts/latest?${search.toString()}`);
  },
  getLatestBySprint: (workspaceId: string, sprintId: string | number, artifactType: string) =>
    apiClient<LatestArtifactResponse>(
      `/api/workspaces/${workspaceId}/sprints/${sprintId}/artifacts/latest?artifactType=${encodeURIComponent(artifactType)}`
    ),
};
