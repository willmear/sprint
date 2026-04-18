"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { authService } from "@/services/auth.service";

export function LoginCard({ redirectTo }: { redirectTo?: string }) {
  return (
    <Card className="w-full max-w-xl overflow-hidden border-white/60 bg-white/85 p-8 shadow-panel backdrop-blur">
      <p className="text-xs uppercase tracking-[0.28em] text-stone-500">Sprint Studio</p>
      <h1 className="mt-3 font-display text-4xl font-bold text-ink sm:text-5xl">Log in with Jira</h1>
      <p className="mt-4 max-w-lg text-sm text-stone-600 sm:text-base">
        Jira login now starts app session. Sign in first, then access only your own workspaces, sprint sync, reviews, and jobs.
      </p>
      <div className="mt-8 flex flex-wrap items-center gap-3">
        <Button onClick={() => authService.startJiraLogin(redirectTo)}>Log in with Jira</Button>
        <p className="text-sm text-stone-500">Authentication required before workspace access.</p>
      </div>
    </Card>
  );
}
