import { env } from "@/config/env";
import { apiClient } from "@/lib/api/client";
import type { CurrentUserResponse } from "@/types/auth";

export const authService = {
  getCurrentUser: () => apiClient<CurrentUserResponse>("/api/auth/me"),
  startJiraLogin: (redirectTo?: string) => {
    const url = new URL("/api/auth/jira/login", env.apiBaseUrl);
    if (redirectTo) {
      url.searchParams.set("redirectTo", redirectTo);
    }
    window.location.assign(url.toString());
  },
  logout: () =>
    apiClient<{ success: boolean }>("/api/auth/logout", {
      method: "POST",
    }),
};
