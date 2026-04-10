import { apiClient } from "@/lib/api/client";
import type {
  GenerateSprintReviewJobResponse,
  GenerateSprintReviewRequest,
  SprintReview,
} from "@/types/review";
import type { SprintContext } from "@/types/sprint";

export const reviewService = {
  get: (workspaceId: string, sprintId: string | number) =>
    apiClient<SprintReview>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/review`),
  getContext: (workspaceId: string, sprintId: string | number) =>
    apiClient<SprintContext>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/review/context`),
  generate: (workspaceId: string, sprintId: string | number, payload: GenerateSprintReviewRequest) =>
    apiClient<SprintReview>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/review/generate`, {
      method: "POST",
      body: payload,
    }),
  enqueue: (workspaceId: string, sprintId: string | number, payload: GenerateSprintReviewRequest) =>
    apiClient<GenerateSprintReviewJobResponse>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/review/generate-job`, {
      method: "POST",
      body: payload,
    }),
};
