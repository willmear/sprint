import { env } from "@/config/env";
import { apiClient, ApiError } from "@/lib/api/client";
import type { ExportFormat, ExportResponse } from "@/types/export";

export const exportService = {
  exportLatestSprintReview: (workspaceId: string, sprintId: string | number, format: ExportFormat) =>
    apiClient<ExportResponse>(
      `/api/workspaces/${workspaceId}/sprints/${sprintId}/export?format=${encodeURIComponent(format)}`
    ),
  exportArtifact: (artifactId: string, format: ExportFormat) =>
    apiClient<ExportResponse>(`/api/artifacts/${artifactId}/export?format=${encodeURIComponent(format)}`),
  async exportDeckAsPowerPoint(workspaceId: string, deckId: string) {
    const response = await fetch(`${env.apiBaseUrl}/api/workspaces/${workspaceId}/slides/decks/${deckId}/export/powerpoint`, {
      credentials: "include",
      cache: "no-store",
    });

    if (!response.ok) {
      const message = await response.text();
      throw new ApiError(message || `Request failed with status ${response.status}.`, response.status, message || undefined);
    }

    const blob = await response.blob();
    const header = response.headers.get("content-disposition");
    const fileName = resolveAttachmentFileName(header) || "deck.pptx";
    return {
      blob,
      contentType: response.headers.get("content-type") || blob.type || "application/vnd.openxmlformats-officedocument.presentationml.presentation",
      fileName,
    };
  },
};

function resolveAttachmentFileName(header: string | null) {
  if (!header) {
    return null;
  }
  const utf8Match = header.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1]);
  }
  const quotedMatch = header.match(/filename="([^"]+)"/i);
  if (quotedMatch?.[1]) {
    return quotedMatch[1];
  }
  const plainMatch = header.match(/filename=([^;]+)/i);
  return plainMatch?.[1]?.trim() || null;
}
