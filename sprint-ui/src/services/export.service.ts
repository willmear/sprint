import { apiClient } from "@/lib/api/client";
import type { ExportFormat, ExportResponse } from "@/types/export";

export const exportService = {
  exportLatestSprintReview: (workspaceId: string, sprintId: string | number, format: ExportFormat) =>
    apiClient<ExportResponse>(
      `/api/workspaces/${workspaceId}/sprints/${sprintId}/export?format=${encodeURIComponent(format)}`
    ),
  exportArtifact: (artifactId: string, format: ExportFormat) =>
    apiClient<ExportResponse>(`/api/artifacts/${artifactId}/export?format=${encodeURIComponent(format)}`),
};
