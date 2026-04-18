"use client";

import Image from "next/image";
import Link from "next/link";
import { use } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { useJiraConnections } from "@/lib/hooks/use-jira";
import { useSprints } from "@/lib/hooks/use-sprints";
import { useWorkspaces } from "@/lib/hooks/use-workspaces";

export default function WorkspaceDetailPage({ params }: { params: Promise<{ workspaceId: string }> }) {
  const { workspaceId } = use(params);
  const workspaces = useWorkspaces();
  const workspace = workspaces.data?.find((candidate) => candidate.id === workspaceId);
  const connections = useJiraConnections(workspaceId);
  const sprints = useSprints(workspaceId);

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Workspace overview</p>
          <h1 className="mt-2 font-display text-4xl font-bold text-ink">{workspace?.name ?? "Workspace"}</h1>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">
            {workspace?.description || "Use this workspace to manage Jira connections, sprint sync, and review generation."}
          </p>
        </div>
        <div className="flex gap-3">
          <Link href={`/workspaces/${workspaceId}/jira`}>
            <Button variant="secondary">Manage Jira</Button>
          </Link>
          <Link href={`/sprints?workspaceId=${workspaceId}`}>
            <Button>Sync sprint</Button>
          </Link>
          <Link href="/workspaces">
            <Button variant="ghost">All workspaces</Button>
          </Link>
        </div>
      </header>

      <div className="grid gap-6 lg:grid-cols-3">
        <Card>
          <p className="text-xs uppercase tracking-[0.2em] text-stone-500">Connections</p>
          <p className="mt-3 font-display text-3xl font-bold text-ink">{connections.data?.length ?? 0}</p>
          <p className="mt-2 text-sm text-stone-600">OAuth-backed Jira connections available for sprint sync.</p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.2em] text-stone-500">Sprints</p>
          <p className="mt-3 font-display text-3xl font-bold text-ink">{sprints.data?.length ?? 0}</p>
          <p className="mt-2 text-sm text-stone-600">Synced sprint snapshots currently stored in the backend.</p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.2em] text-stone-500">Latest state</p>
          {connections.data?.[0] ? (
            <div className="mt-3 flex items-center gap-3">
              <Avatar
                avatarUrl={connections.data[0].externalAccountAvatarUrl}
                displayName={connections.data[0].externalAccountDisplayName || connections.data[0].baseUrl}
              />
              <p className="text-sm text-stone-700">
                Connected Jira account: {connections.data[0].externalAccountDisplayName || connections.data[0].baseUrl}
              </p>
            </div>
          ) : (
            <p className="mt-3 text-sm text-stone-700">No Jira workspace connection yet.</p>
          )}
        </Card>
      </div>

      <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <h2 className="font-display text-2xl font-bold text-ink">Jira connections</h2>
              <p className="mt-2 text-sm text-stone-600">Authorize Jira for this workspace before syncing a sprint.</p>
            </div>
            <Link href={`/workspaces/${workspaceId}/jira`}>
              <Button variant="secondary">Open Jira page</Button>
            </Link>
          </div>

          <div className="mt-6 space-y-3">
            {connections.isLoading ? <p className="text-sm text-stone-600">Loading connections...</p> : null}
            {connections.data?.map((connection) => (
              <div key={connection.id} className="rounded-3xl border border-line bg-cloud p-4">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="font-medium text-ink">{connection.externalAccountDisplayName || connection.baseUrl}</p>
                    <p className="mt-2 text-sm text-stone-600">{connection.baseUrl}</p>
                  </div>
                  <Badge tone={connection.status === "ACTIVE" ? "success" : "warning"}>{connection.status}</Badge>
                </div>
              </div>
            ))}
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between gap-3">
            <div>
              <h2 className="font-display text-2xl font-bold text-ink">Sprint snapshots</h2>
              <p className="mt-2 text-sm text-stone-600">Sync a sprint first, then open sprint detail or jump straight to review generation.</p>
            </div>
            <Link href={`/sprints?workspaceId=${workspaceId}`}>
              <Button variant="secondary">Open sprint flow</Button>
            </Link>
          </div>

          <div className="mt-6 space-y-3">
            {sprints.isLoading ? <p className="text-sm text-stone-600">Loading sprints...</p> : null}
            {!sprints.isLoading && sprints.data?.length === 0 ? (
              <div className="rounded-3xl border border-line bg-cloud p-4">
                <p className="font-medium text-ink">No sprint snapshots yet</p>
                <p className="mt-2 text-sm text-stone-600">
                  Open the sprint flow and sync a Jira sprint by ID to make sprint detail and review generation available.
                </p>
              </div>
            ) : null}
            {sprints.data?.map((sprint) => (
              <div key={sprint.sprintId} className="rounded-3xl border border-line p-4">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                  <div>
                    <p className="text-lg font-semibold text-ink">{sprint.name}</p>
                    <p className="mt-1 text-sm text-stone-600">{sprint.state}</p>
                  </div>
                  <div className="flex gap-2">
                    <Link href={`/sprints/${workspaceId}/${sprint.sprintId}`}>
                      <Button variant="secondary">Sprint detail</Button>
                    </Link>
                    <Link href={`/review/${workspaceId}/${sprint.sprintId}`}>
                      <Button>Review</Button>
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}

function Avatar({ avatarUrl, displayName }: { avatarUrl?: string | null; displayName: string }) {
  if (avatarUrl) {
    return (
      <Image
        alt={displayName}
        className="rounded-full border border-line object-cover"
        height={40}
        loader={({ src }) => src}
        src={avatarUrl}
        unoptimized
        width={40}
      />
    );
  }

  return (
    <div className="flex h-10 w-10 items-center justify-center rounded-full border border-line bg-sand text-sm font-semibold text-ink">
      {displayName.slice(0, 1).toUpperCase()}
    </div>
  );
}
