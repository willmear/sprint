"use client";

import { useMutation } from "@tanstack/react-query";

import { exportService } from "@/services/export.service";
import type { ExportFormat } from "@/types/export";

export function useExportLatestSprintReview(workspaceId: string, sprintId: string | number) {
  return useMutation({
    mutationFn: (format: ExportFormat) => exportService.exportLatestSprintReview(workspaceId, sprintId, format),
  });
}

export function useExportArtifact() {
  return useMutation({
    mutationFn: ({ artifactId, format }: { artifactId: string; format: ExportFormat }) =>
      exportService.exportArtifact(artifactId, format),
  });
}
