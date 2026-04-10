"use client";

import Link from "next/link";

import { CreateWorkspaceForm } from "@/components/forms/create-workspace-form";
import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { useWorkspaces } from "@/lib/hooks/use-workspaces";

export default function WorkspacesPage() {
  const workspaces = useWorkspaces();

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Workspaces</p>
          <h1 className="mt-2 font-display text-4xl font-bold text-ink">Team and product spaces</h1>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">
            Each workspace isolates Jira connections, synced sprint data, generated reviews, and job history.
          </p>
        </div>
        <Badge>{workspaces.data?.length ?? 0} total</Badge>
      </header>

      <div className="grid gap-6 xl:grid-cols-[0.8fr_1.2fr]">
        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Create a workspace</h2>
          <p className="mt-2 text-sm text-stone-600">Start a new operating surface for one product or delivery team.</p>
          <div className="mt-6">
            <CreateWorkspaceForm />
          </div>
        </Card>

        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Existing workspaces</h2>
          <p className="mt-2 text-sm text-stone-600">Pick a workspace to connect Jira, sync sprints, or inspect the latest review.</p>

          <div className="mt-6 grid gap-4">
            {workspaces.isLoading ? <p className="text-sm text-stone-600">Loading workspaces...</p> : null}
            {workspaces.error ? <p className="text-sm text-rose-600">{workspaces.error.message}</p> : null}
            {workspaces.data?.map((workspace) => (
              <Link key={workspace.id} href={`/workspaces/${workspace.id}`}>
                <div className="rounded-3xl border border-line bg-cloud p-5 transition hover:-translate-y-1 hover:border-pine">
                  <div className="flex items-start justify-between gap-4">
                    <div>
                      <h3 className="text-lg font-semibold text-ink">{workspace.name}</h3>
                      <p className="mt-2 text-sm text-stone-600">{workspace.description || "No description provided."}</p>
                    </div>
                    <Badge>Open</Badge>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
