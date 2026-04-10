import Link from "next/link";

import { Card } from "@/components/ui/card";

export default function ReviewLandingPage() {
  return (
    <div className="space-y-6">
      <header>
        <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Review</p>
        <h1 className="mt-2 font-display text-4xl font-bold text-ink">Sprint review workspace</h1>
        <p className="mt-2 max-w-2xl text-sm text-stone-600">
          Reviews are scoped to a workspace and sprint. Start from a synced sprint to generate or inspect the structured artifact.
        </p>
      </header>

      <Card>
        <p className="text-sm text-stone-700">
          Open <Link className="font-semibold text-pine" href="/sprints">Sprints</Link> to choose a workspace and sprint, or jump in from a workspace overview.
        </p>
      </Card>
    </div>
  );
}
