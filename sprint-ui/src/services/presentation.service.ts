import { ApiError, apiClient } from "@/lib/api/client";
import type { AddSlideRequest, PresentationDeck, ReorderSlidesRequest, UpdateDeckRequest, UpdateSlideRequest } from "@/types/presentation";

export const presentationService = {
  async getOrCreateDeck(workspaceId: string, sprintId: string | number) {
    try {
      return await apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/slides/deck`);
    } catch (error) {
      if (error instanceof ApiError && error.status === 404) {
        return apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/slides/deck`, {
          method: "POST",
        });
      }
      throw error;
    }
  },
  getLatestDeck: (workspaceId: string, sprintId: string | number) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/slides/deck`),
  createDeck: (workspaceId: string, sprintId: string | number) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/sprints/${sprintId}/slides/deck`, {
      method: "POST",
    }),
  getDeck: (workspaceId: string, deckId: string) => apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}`),
  saveDeck: (workspaceId: string, deckId: string, payload: UpdateDeckRequest) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}`, {
      method: "PUT",
      body: payload,
    }),
  updateSlide: (workspaceId: string, deckId: string, slideId: string, payload: UpdateSlideRequest) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}/slides/${slideId}`, {
      method: "PUT",
      body: payload,
    }),
  reorderSlides: (workspaceId: string, deckId: string, payload: ReorderSlidesRequest) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}/slides/reorder`, {
      method: "PUT",
      body: payload,
    }),
  addSlide: (workspaceId: string, deckId: string, payload: AddSlideRequest) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}/slides`, {
      method: "POST",
      body: payload,
    }),
  duplicateSlide: (workspaceId: string, deckId: string, slideId: string) =>
    apiClient<PresentationDeck>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}/slides/${slideId}/duplicate`, {
      method: "POST",
      body: {},
    }),
  deleteSlide: (workspaceId: string, deckId: string, slideId: string) =>
    apiClient<void>(`/api/workspaces/${workspaceId}/slides/decks/${deckId}/slides/${slideId}`, {
      method: "DELETE",
    }),
};
