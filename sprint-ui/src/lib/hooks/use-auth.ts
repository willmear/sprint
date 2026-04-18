"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { authService } from "@/services/auth.service";
import type { CurrentUserResponse } from "@/types/auth";

const unauthenticatedState: CurrentUserResponse = {
  authenticated: false,
  user: null,
};

export function useAuth() {
  const query = useQuery({
    queryKey: ["auth", "me"],
    queryFn: authService.getCurrentUser,
    retry: false,
  });

  const authState = query.data ?? unauthenticatedState;

  return {
    ...query,
    authenticated: authState.authenticated,
    user: authState.user,
    isUnauthenticated: !query.isLoading && !authState.authenticated,
  };
}

export function useLogout() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authService.logout,
    onSuccess: async () => {
      queryClient.setQueryData(["auth", "me"], unauthenticatedState);
      await queryClient.invalidateQueries();
    },
  });
}
