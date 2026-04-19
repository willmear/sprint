import { apiClient } from "@/lib/api/client";
import type { UserCreditSummary, UserProfile } from "@/types/profile";

export const profileService = {
  getCurrentUserProfile: () => apiClient<UserProfile>("/api/profile"),
  getCurrentUserCredits: () => apiClient<UserCreditSummary>("/api/profile/credits"),
};
