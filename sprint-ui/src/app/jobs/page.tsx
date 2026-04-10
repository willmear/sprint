"use client";

import { useState } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useJobs, useRetryJob } from "@/lib/hooks/use-jobs";

export default function JobsPage() {
  const [workspaceId, setWorkspaceId] = useState("");
  const jobs = useJobs({ workspaceId: workspaceId || undefined });
  const retryJob = useRetryJob();

  return (
    <div className="space-y-6">
      <header>
        <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Jobs</p>
        <h1 className="mt-2 font-display text-4xl font-bold text-ink">Job queue visibility</h1>
        <p className="mt-2 max-w-2xl text-sm text-stone-600">Track background review generation and operational retries from the same dashboard.</p>
      </header>

      <Card>
        <div className="grid gap-4 sm:grid-cols-[1fr_auto]">
          <div>
            <label className="text-sm font-medium text-stone-700" htmlFor="workspace-filter">
              Filter by workspace ID
            </label>
            <Input
              id="workspace-filter"
              placeholder="Optional workspace UUID"
              value={workspaceId}
              onChange={(event) => setWorkspaceId(event.target.value)}
            />
          </div>
          <div className="self-end">
            <Button variant="secondary" onClick={() => jobs.refetch()}>
              Refresh
            </Button>
          </div>
        </div>
      </Card>

      <div className="grid gap-4">
        {jobs.isLoading ? <p className="text-sm text-stone-600">Loading jobs...</p> : null}
        {jobs.error ? <p className="text-sm text-rose-600">{jobs.error.message}</p> : null}
        {jobs.data?.map((job) => (
          <Card key={job.id}>
            <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div>
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="text-lg font-semibold text-ink">{job.jobType}</h2>
                  <Badge tone={job.status === "COMPLETED" ? "success" : job.status === "FAILED" ? "danger" : "default"}>{job.status}</Badge>
                </div>
                <p className="mt-2 text-sm text-stone-600">Workspace {job.workspaceId}</p>
                <p className="mt-1 text-xs uppercase tracking-[0.16em] text-stone-500">
                  Attempts {job.attemptCount}/{job.maxAttempts} • Updated {new Date(job.updatedAt).toLocaleString()}
                </p>
              </div>
              <div className="flex gap-2">
                {job.status === "FAILED" ? (
                  <Button
                    disabled={retryJob.isPending}
                    onClick={() => retryJob.mutate(job.id)}
                  >
                    Retry
                  </Button>
                ) : null}
              </div>
            </div>
          </Card>
        ))}
      </div>
      {retryJob.error ? <p className="text-sm text-rose-600">{retryJob.error.message}</p> : null}
    </div>
  );
}
