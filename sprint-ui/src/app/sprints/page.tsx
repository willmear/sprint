"use client";

import { FormEvent } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useEffect, useMemo, useState } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useJiraConnections } from "@/lib/hooks/use-jira";
import { useAvailableJiraSprints, useSprints, useSyncSprint } from "@/lib/hooks/use-sprints";
import { useWorkspaces } from "@/lib/hooks/use-workspaces";

export default function SprintsPage() {
  return (
    <Suspense fallback={<p className="text-sm text-stone-600">Loading sprint dashboard...</p>}>
      <SprintsPageContent />
    </Suspense>
  );
}

function SprintsPageContent() {
  const searchParams = useSearchParams();
  const workspaces = useWorkspaces();
  const [selectedWorkspaceId, setSelectedWorkspaceId] = useState<string>("");
  const selectedWorkspace = useMemo(
    () => workspaces.data?.find((workspace) => workspace.id === selectedWorkspaceId),
    [selectedWorkspaceId, workspaces.data]
  );
  const connections = useJiraConnections(selectedWorkspaceId || undefined);
  const activeConnection = connections.data?.find((connection) => connection.status === "ACTIVE") ?? connections.data?.[0];
  const availableJiraSprints = useAvailableJiraSprints(selectedWorkspaceId || undefined, activeConnection?.id);
  const sprints = useSprints(selectedWorkspaceId || undefined);
  const syncSprint = useSyncSprint(selectedWorkspaceId, activeConnection?.id);
  const [sprintSearch, setSprintSearch] = useState("");
  const [selectedAvailableSprintId, setSelectedAvailableSprintId] = useState("");
  const [syncingSprintId, setSyncingSprintId] = useState<string | null>(null);

  useEffect(() => {
    const workspaceId = searchParams.get("workspaceId");
    if (workspaceId) {
      setSelectedWorkspaceId(workspaceId);
    }
  }, [searchParams]);

  useEffect(() => {
    setSprintSearch("");
    setSelectedAvailableSprintId("");
  }, [selectedWorkspaceId]);

  async function handleManualSync(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedAvailableSprintId) {
      return;
    }
    setSyncingSprintId(selectedAvailableSprintId);
    try {
      await syncSprint.mutateAsync(selectedAvailableSprintId);
    } finally {
      setSyncingSprintId(null);
    }
  }

  async function handleExistingSprintSync(sprintId: number) {
    const normalizedSprintId = String(sprintId);
    setSyncingSprintId(normalizedSprintId);
    try {
      await syncSprint.mutateAsync(sprintId);
    } finally {
      setSyncingSprintId(null);
    }
  }

  const filteredAvailableSprints = useMemo(() => {
    const normalized = sprintSearch.trim().toLowerCase();
    if (!normalized) {
      return availableJiraSprints.data ?? [];
    }
    return (availableJiraSprints.data ?? []).filter((sprint) => {
      const haystack = `${sprint.sprintName} ${sprint.boardName ?? ""} ${sprint.state}`.toLowerCase();
      return haystack.includes(normalized);
    });
  }, [availableJiraSprints.data, sprintSearch]);
  const selectedAvailableSprint = useMemo(
    () => (availableJiraSprints.data ?? []).find((sprint) => String(sprint.sprintId) === selectedAvailableSprintId),
    [availableJiraSprints.data, selectedAvailableSprintId]
  );

  return (
    <div className="space-y-6">
      <header>
        <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Sprints</p>
        <h1 className="mt-2 font-display text-4xl font-bold text-ink">Sprint sync and inspection</h1>
        <p className="mt-2 max-w-2xl text-sm text-stone-600">
          Choose a workspace, verify its Jira connection, and sync or inspect the local sprint snapshot.
        </p>
      </header>

      <Card>
        <div className="grid gap-4 lg:grid-cols-[1fr_auto]">
          <div>
            <label className="text-sm font-medium text-stone-700" htmlFor="workspace-select">
              Workspace
            </label>
            <select
              id="workspace-select"
              className="mt-2 w-full rounded-2xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none focus:border-pine"
              value={selectedWorkspaceId}
              onChange={(event) => setSelectedWorkspaceId(event.target.value)}
            >
              <option value="">Select a workspace</option>
              {workspaces.data?.map((workspace) => (
                <option key={workspace.id} value={workspace.id}>
                  {workspace.name}
                </option>
              ))}
            </select>
          </div>
          {selectedWorkspace ? (
            <div className="self-end">
              <Link href={`/workspaces/${selectedWorkspace.id}/jira`}>
                <Button variant="secondary">Manage Jira connection</Button>
              </Link>
            </div>
          ) : null}
        </div>
      </Card>

      {selectedWorkspaceId ? (
        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Pick a Jira sprint</h2>
          <p className="mt-2 text-sm text-stone-600">
            Search the connected Jira sprints by name, choose one from the dropdown, and sync it into the local workspace snapshot.
          </p>

          <form className="mt-6 space-y-4" onSubmit={handleManualSync}>
            <div className="space-y-2">
              <label className="text-sm font-medium text-stone-700" htmlFor="sprint-search">
                Search sprints
              </label>
              <Input
                id="sprint-search"
                placeholder="Start typing a sprint name"
                value={sprintSearch}
                onChange={(event) => setSprintSearch(event.target.value)}
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-stone-700" htmlFor="available-sprint-select">
                Available Jira sprints
              </label>
              <select
                id="available-sprint-select"
                className="w-full rounded-2xl border border-line bg-white px-4 py-3 text-sm text-ink outline-none focus:border-pine"
                value={selectedAvailableSprintId}
                onChange={(event) => setSelectedAvailableSprintId(event.target.value)}
                disabled={!activeConnection || availableJiraSprints.isLoading}
              >
                <option value="">Select a sprint</option>
                {filteredAvailableSprints.map((sprint) => (
                  <option key={sprint.sprintId} value={sprint.sprintId}>
                    {sprint.sprintName} {sprint.boardName ? `• ${sprint.boardName}` : ""} • {sprint.state}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex flex-wrap items-center gap-3">
              <Button
                disabled={!activeConnection || syncSprint.isPending || !selectedAvailableSprintId}
                type="submit"
              >
                {syncSprint.isPending && syncingSprintId === selectedAvailableSprintId ? "Syncing..." : "Sync sprint"}
              </Button>
              {selectedAvailableSprint ? (
                <p className="text-sm text-stone-600">
                  Selected: {selectedAvailableSprint.sprintName}
                </p>
              ) : null}
            </div>
          </form>

          {availableJiraSprints.isLoading ? <p className="mt-4 text-sm text-stone-600">Loading available Jira sprints...</p> : null}
          {availableJiraSprints.error ? <p className="mt-4 text-sm text-rose-600">{availableJiraSprints.error.message}</p> : null}
          {!activeConnection && !connections.isLoading ? (
            <p className="mt-4 text-sm text-rose-600">Connect Jira for this workspace before syncing a sprint.</p>
          ) : null}
          {activeConnection && !availableJiraSprints.isLoading && filteredAvailableSprints.length === 0 ? (
            <p className="mt-4 text-sm text-stone-600">
              {availableJiraSprints.data?.length
                ? "No Jira sprints matched that search."
                : "Jira did not return any discoverable sprints for this workspace yet."}
            </p>
          ) : null}
          <p className="mt-4 text-sm text-stone-600">
            After the sync completes, use the sprint cards below or jump directly to the review page for that sprint.
          </p>
        </Card>
      ) : null}

      <div className="grid gap-6">
        {sprints.isLoading ? <p className="text-sm text-stone-600">Loading sprints...</p> : null}
        {sprints.error ? <p className="text-sm text-rose-600">{sprints.error.message}</p> : null}
        {!sprints.isLoading && selectedWorkspaceId && sprints.data?.length === 0 ? (
          <Card>
            <p className="font-semibold text-ink">No synced sprints yet</p>
            <p className="mt-2 text-sm text-stone-600">
              Use the sprint ID form above to sync your first sprint from Jira. Once it is synced, it will appear here with links to detail and review.
            </p>
          </Card>
        ) : null}
        {sprints.data?.map((sprint) => (
          <Card key={sprint.sprintId}>
            <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div>
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="font-display text-2xl font-bold text-ink">{sprint.name}</h2>
                  <Badge tone={sprint.state === "ACTIVE" ? "success" : "default"}>{sprint.state}</Badge>
                </div>
                <p className="mt-2 text-sm text-stone-600">{sprint.goal || "No sprint goal captured."}</p>
                <p className="mt-3 text-xs uppercase tracking-[0.14em] text-stone-500">
                  Sprint #{sprint.sprintId} {sprint.syncedAt ? `• Synced ${new Date(sprint.syncedAt).toLocaleString()}` : ""}
                </p>
              </div>
              <div className="flex flex-wrap gap-2">
                <Button
                  disabled={!connections.data?.[0] || syncSprint.isPending}
                  onClick={() => void handleExistingSprintSync(sprint.sprintId)}
                >
                  {syncSprint.isPending && syncingSprintId === String(sprint.sprintId) ? "Syncing..." : "Sync sprint"}
                </Button>
                <Link href={`/sprints/${selectedWorkspaceId}/${sprint.sprintId}`}>
                  <Button variant="secondary">Open detail</Button>
                </Link>
                <Link href={`/review/${selectedWorkspaceId}/${sprint.sprintId}`}>
                  <Button variant="ghost">Review</Button>
                </Link>
                <Link href={`/workspaces/${selectedWorkspaceId}/sprints/${sprint.sprintId}/slides`}>
                  <Button variant="secondary">Edit slides</Button>
                </Link>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {syncSprint.data ? (
        <Card className="border-emerald-200 bg-emerald-50">
          <p className="font-semibold text-emerald-900">Sprint sync complete</p>
          <p className="mt-2 text-sm text-emerald-800">
            {syncSprint.data.sprintName}: {syncSprint.data.issueCount} issues, {syncSprint.data.commentCount} comments, {syncSprint.data.changelogEventCount} changelog events.
          </p>
          <div className="mt-4 flex flex-wrap gap-2">
            <Link href={`/sprints/${syncSprint.data.workspaceId}/${syncSprint.data.sprintId}`}>
              <Button variant="secondary">Open sprint detail</Button>
            </Link>
            <Link href={`/review/${syncSprint.data.workspaceId}/${syncSprint.data.sprintId}`}>
              <Button>Generate review</Button>
            </Link>
            <Link href={`/workspaces/${syncSprint.data.workspaceId}/sprints/${syncSprint.data.sprintId}/slides`}>
              <Button variant="secondary">Edit slides</Button>
            </Link>
          </div>
        </Card>
      ) : null}
      {syncSprint.error ? <p className="text-sm text-rose-600">{syncSprint.error.message}</p> : null}
    </div>
  );
}
