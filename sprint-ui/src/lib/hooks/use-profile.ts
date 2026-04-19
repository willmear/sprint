"use client";

import { useQuery } from "@tanstack/react-query";

import { profileService } from "@/services/profile.service";

export function useCurrentUserProfile() {
  return useQuery({
    queryKey: ["profile", "current-user"],
    queryFn: profileService.getCurrentUserProfile,
  });
}

export function useCurrentUserCredits() {
  return useQuery({
    queryKey: ["profile", "credits"],
    queryFn: profileService.getCurrentUserCredits,
  });
}
