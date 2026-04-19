"use client";

import { Card } from "@/components/ui/card";
import type { UserProfile } from "@/types/profile";

import { ProfileAvatar } from "./profile-avatar";

export function ProfileCard({ profile }: { profile: UserProfile }) {
  return (
    <Card>
      <div className="flex items-start gap-4">
        <ProfileAvatar avatarUrl={profile.avatarUrl} displayName={profile.displayName} size={64} />
        <div className="min-w-0 flex-1">
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Profile</p>
          <h1 className="mt-2 text-2xl font-semibold text-ink">{profile.displayName}</h1>
          <p className="mt-1 text-sm text-stone-600">{profile.email || "No email shared by Atlassian"}</p>
          <div className="mt-5 grid gap-4 sm:grid-cols-2">
            <Detail label="Auth provider" value={profile.authProvider} />
            <Detail label="Last login" value={formatDateTime(profile.lastLoginAt)} />
          </div>
        </div>
      </div>
    </Card>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-stone-50 px-4 py-3">
      <p className="text-xs uppercase tracking-[0.18em] text-stone-500">{label}</p>
      <p className="mt-2 text-sm font-medium text-ink">{value}</p>
    </div>
  );
}

function formatDateTime(value?: string | null) {
  if (!value) {
    return "Not available";
  }

  try {
    return new Intl.DateTimeFormat(undefined, {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(value));
  } catch {
    return value;
  }
}
