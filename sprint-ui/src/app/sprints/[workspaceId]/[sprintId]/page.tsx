"use client";

import Link from "next/link";
import { use } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { useSprint, useSprintIssues } from "@/lib/hooks/use-sprints";

export default function SprintDetailPage({
  params,
}: {
  params: Promise<{ workspaceId: string; sprintId: string }>;
}) {
  const { workspaceId, sprintId } = use(params);
  const sprint = useSprint(workspaceId, sprintId);
  const issues = useSprintIssues(workspaceId, sprintId);

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Sprint detail</p>
          <h1 className="mt-2 font-display text-4xl font-bold text-ink">{sprint.data?.name ?? `Sprint ${sprintId}`}</h1>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">{sprint.data?.goal || "No sprint goal captured."}</p>
        </div>
        <div className="flex flex-wrap gap-3">
          <Link href={`/review/${workspaceId}/${sprintId}`}>
            <Button>Open review</Button>
          </Link>
          <Link href={`/workspaces/${workspaceId}/sprints/${sprintId}/slides`}>
            <Button type="button" variant="secondary">Edit slides</Button>
          </Link>
        </div>
      </header>

      <div className="grid gap-6 md:grid-cols-4">
        <Card>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">State</p>
          <p className="mt-3 text-lg font-semibold text-ink">{sprint.data?.state ?? "-"}</p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Issues</p>
          <p className="mt-3 text-lg font-semibold text-ink">{sprint.data?.issueCount ?? issues.data?.length ?? 0}</p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Start</p>
          <p className="mt-3 text-sm font-semibold text-ink">
            {sprint.data?.startDate ? new Date(sprint.data.startDate).toLocaleString() : "Not set"}
          </p>
        </Card>
        <Card>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">End</p>
          <p className="mt-3 text-sm font-semibold text-ink">
            {sprint.data?.endDate ? new Date(sprint.data.endDate).toLocaleString() : "Not set"}
          </p>
        </Card>
      </div>

      <Card>
        <h2 className="font-display text-2xl font-bold text-ink">Issue inventory</h2>
        <p className="mt-2 text-sm text-stone-600">Ticket-level data feeding the review prompt and final artifact.</p>

        <div className="mt-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-line text-left text-sm">
            <thead>
              <tr className="text-stone-500">
                <th className="pb-3 pr-4 font-medium">Key</th>
                <th className="pb-3 pr-4 font-medium">Summary</th>
                <th className="pb-3 pr-4 font-medium">Status</th>
                <th className="pb-3 pr-4 font-medium">Type</th>
                <th className="pb-3 pr-4 font-medium">Priority</th>
                <th className="pb-3 pr-4 font-medium">Assignee</th>
                <th className="pb-3 font-medium">Story points</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-line">
              {issues.data?.map((issue) => (
                <tr key={issue.issueKey} className="align-top">
                  <td className="py-4 pr-4 font-semibold text-ink">{issue.issueKey}</td>
                  <td className="py-4 pr-4">
                    <div>
                      <p className="font-medium text-ink">{issue.summary}</p>
                      <p className="mt-1 max-w-md text-xs text-stone-500">{issue.description || "No description."}</p>
                    </div>
                  </td>
                  <td className="py-4 pr-4">
                    <Badge tone={issue.status.toLowerCase().includes("done") ? "success" : "default"}>{issue.status}</Badge>
                  </td>
                  <td className="py-4 pr-4 text-stone-700">{issue.issueType}</td>
                  <td className="py-4 pr-4 text-stone-700">{issue.priority || "-"}</td>
                  <td className="py-4 pr-4 text-stone-700">{issue.assigneeDisplayName || "Unassigned"}</td>
                  <td className="py-4 text-stone-700">{issue.storyPoints ?? "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
