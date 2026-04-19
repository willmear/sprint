"use client";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { useLogout } from "@/lib/hooks/use-auth";
import type { UserCreditSummary } from "@/types/profile";
import { useRouter } from "next/navigation";

export function CreditsCard({ credits }: { credits: UserCreditSummary }) {
  const router = useRouter();
  const logout = useLogout();
  const usagePercent = credits.dailyLimit > 0 ? Math.min(100, Math.round((credits.usedToday / credits.dailyLimit) * 100)) : 0;

  async function handleLogout() {
    await logout.mutateAsync();
    router.replace("/login");
  }

  return (
    <Card>
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Daily sprint review generations</p>
          <h2 className="mt-2 text-2xl font-semibold text-ink">
            {credits.usedToday} / {credits.dailyLimit} used
          </h2>
          <p className="mt-2 text-sm text-stone-600">Usage date: {formatUsageDate(credits.usageDate)}</p>
        </div>
        <div className="flex items-center gap-3">
          <Badge tone={credits.canGenerate ? "success" : "warning"}>
            {credits.canGenerate ? "Available" : "Limit reached"}
          </Badge>
          <Button disabled={logout.isPending} variant="secondary" onClick={() => void handleLogout()}>
            {logout.isPending ? "Logging out..." : "Log out"}
          </Button>
        </div>
      </div>

      <div className="mt-6">
        <div className="h-3 overflow-hidden rounded-full bg-stone-100">
          <div className={`h-full rounded-full ${credits.canGenerate ? "bg-emerald-500" : "bg-amber-500"}`} style={{ width: `${usagePercent}%` }} />
        </div>
        <div className="mt-4 grid gap-4 sm:grid-cols-3">
          <Metric label="Remaining today" value={`${credits.remainingToday}`} />
          <Metric label="Used today" value={`${credits.usedToday}`} />
          <Metric label="Daily limit" value={`${credits.dailyLimit}`} />
        </div>
      </div>

      {!credits.canGenerate ? (
        <div className="mt-6 rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          You&apos;ve used all sprint review generations for today.
        </div>
      ) : null}

      {logout.error ? <p className="mt-4 text-sm text-rose-600">{logout.error.message}</p> : null}
    </Card>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-stone-50 px-4 py-3">
      <p className="text-xs uppercase tracking-[0.18em] text-stone-500">{label}</p>
      <p className="mt-2 text-lg font-semibold text-ink">{value}</p>
    </div>
  );
}

function formatUsageDate(value: string) {
  try {
    return new Intl.DateTimeFormat(undefined, {
      dateStyle: "medium",
      timeZone: "UTC",
    }).format(new Date(`${value}T00:00:00Z`));
  } catch {
    return value;
  }
}
