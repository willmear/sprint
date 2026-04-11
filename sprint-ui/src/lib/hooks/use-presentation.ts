"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { presentationService } from "@/services/presentation.service";
import type { AddSlideRequest, ReorderSlidesRequest, UpdateDeckRequest, UpdateSlideRequest } from "@/types/presentation";

export function usePresentationDeck(workspaceId?: string, sprintId?: string | number) {
  return useQuery({
    queryKey: ["presentation-deck", workspaceId, sprintId],
    queryFn: () => presentationService.getOrCreateDeck(workspaceId!, sprintId!),
    enabled: Boolean(workspaceId && sprintId),
  });
}

export function useSavePresentationDeck(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, payload }: { deckId: string; payload: UpdateDeckRequest }) =>
      presentationService.saveDeck(workspaceId, deckId, payload),
    onSuccess: async (deck) => {
      await queryClient.setQueryData(["presentation-deck", workspaceId, sprintId], deck);
    },
  });
}

export function useAddSlide(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, payload }: { deckId: string; payload: AddSlideRequest }) =>
      presentationService.addSlide(workspaceId, deckId, payload),
    onSuccess: async (deck) => {
      await queryClient.setQueryData(["presentation-deck", workspaceId, sprintId], deck);
    },
  });
}

export function useDuplicateSlide(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, slideId }: { deckId: string; slideId: string }) =>
      presentationService.duplicateSlide(workspaceId, deckId, slideId),
    onSuccess: async (deck) => {
      await queryClient.setQueryData(["presentation-deck", workspaceId, sprintId], deck);
    },
  });
}

export function useDeleteSlide(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, slideId }: { deckId: string; slideId: string }) =>
      presentationService.deleteSlide(workspaceId, deckId, slideId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["presentation-deck", workspaceId, sprintId] });
    },
  });
}

export function useReorderSlides(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, payload }: { deckId: string; payload: ReorderSlidesRequest }) =>
      presentationService.reorderSlides(workspaceId, deckId, payload),
    onSuccess: async (deck) => {
      await queryClient.setQueryData(["presentation-deck", workspaceId, sprintId], deck);
    },
  });
}

export function useUpdateSlide(workspaceId: string, sprintId: string | number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ deckId, slideId, payload }: { deckId: string; slideId: string; payload: UpdateSlideRequest }) =>
      presentationService.updateSlide(workspaceId, deckId, slideId, payload),
    onSuccess: async (deck) => {
      await queryClient.setQueryData(["presentation-deck", workspaceId, sprintId], deck);
    },
  });
}
