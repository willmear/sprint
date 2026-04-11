import { apiClient } from "@/lib/api/client";
import type { Artifact, ArtifactListResponse, LatestArtifactResponse } from "@/types/api";

export const artifactService = {
  getById: (artifactId: string) => apiClient<Artifact>(`/api/artifacts/${artifactId}`),
  listByWorkspace: (workspaceId: string, params?: { artifactType?: string; referenceType?: string; referenceId?: string }) => {
    const search = new URLSearchParams();
    if (params?.artifactType) search.set("artifactType", params.artifactType);
    if (params?.referenceType) search.set("referenceType", params.referenceType);
    if (params?.referenceId) search.set("referenceId", params.referenceId);
    const suffix = search.toString() ? `?${search.toString()}` : "";
    return apiClient<ArtifactListResponse>(`/api/workspaces/${workspaceId}/artifacts${suffix}`);
  },
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
