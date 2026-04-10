import Link from "next/link";

import { Card } from "@/components/ui/card";

const launchpad = [
  {
    href: "/workspaces",
    title: "Workspace setup",
    description: "Create a workspace, define its scope, and establish the tenant boundary for Jira and review generation.",
  },
  {
    href: "/sprints",
    title: "Sprint operations",
    description: "Inspect synced sprints, see issue-level status, and trigger new sync runs against a connected Jira workspace.",
  },
  {
    href: "/jobs",
    title: "Job monitor",
    description: "Track asynchronous review generation and retry failed jobs when the pipeline needs intervention.",
  },
];

export default function HomePage() {
  return (
    <div className="space-y-6">
      <Card className="overflow-hidden bg-ink text-sand">
        <div className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-sand/60">Delivery intelligence</p>
            <h1 className="mt-3 font-display text-4xl font-bold leading-tight sm:text-5xl">
              Turn synced Jira work into a sprint review your team can actually present.
            </h1>
            <p className="mt-4 max-w-2xl text-sm text-sand/80 sm:text-base">
              Sprint Studio keeps the workflow inside one operating surface: connect Jira, sync sprint data, generate a structured review, and inspect the underlying issues and comments that drove it.
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-1">
            <Metric label="Core workflow" value="Workspace -> Jira -> Sync -> Review" />
            <Metric label="Review output" value="Summary, themes, blockers, notes" />
            <Metric label="Backend" value="Spring Boot REST + artifact persistence" />
          </div>
        </div>
      </Card>

      <div className="grid gap-6 lg:grid-cols-3">
        {launchpad.map((item) => (
          <Link key={item.href} href={item.href}>
            <Card className="h-full transition hover:-translate-y-1 hover:border-pine">
              <p className="text-xs uppercase tracking-[0.22em] text-stone-500">Start here</p>
              <h2 className="mt-3 font-display text-2xl font-bold text-ink">{item.title}</h2>
              <p className="mt-3 text-sm text-stone-700">{item.description}</p>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-[28px] border border-white/15 bg-white/5 p-4">
      <p className="text-xs uppercase tracking-[0.2em] text-sand/50">{label}</p>
      <p className="mt-2 text-lg font-semibold text-sand">{value}</p>
    </div>
  );
}
