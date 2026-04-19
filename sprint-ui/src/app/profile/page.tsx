"use client";

import { CreditsCard } from "@/components/profile/credits-card";
import { ProfileCard } from "@/components/profile/profile-card";
import { Card } from "@/components/ui/card";
import { useCurrentUserCredits, useCurrentUserProfile } from "@/lib/hooks/use-profile";

export default function ProfilePage() {
  const profile = useCurrentUserProfile();
  const credits = useCurrentUserCredits();

  const isLoading = profile.isLoading || credits.isLoading;
  const hasError = profile.error || credits.error;

  return (
    <div className="space-y-6">
      <section>
        <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Account</p>
        <h1 className="mt-2 text-3xl font-semibold text-ink">Profile</h1>
        <p className="mt-2 max-w-2xl text-sm text-stone-600">Review your logged-in Atlassian identity, daily sprint review allowance, and session controls.</p>
      </section>

      {isLoading ? (
        <Card className="text-sm text-stone-600">Loading your profile...</Card>
      ) : null}

      {hasError ? (
        <Card className="border-rose-200 bg-rose-50 text-sm text-rose-700">
          <p className="font-semibold">We couldn&apos;t load your profile details.</p>
          <p className="mt-2">{profile.error?.message || credits.error?.message || "Please try again."}</p>
        </Card>
      ) : null}

      {!isLoading && profile.data ? <ProfileCard profile={profile.data} /> : null}
      {!isLoading && credits.data ? <CreditsCard credits={credits.data} /> : null}
    </div>
  );
}
