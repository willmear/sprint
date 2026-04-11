"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useJobs, useRetryJob } from "@/lib/hooks/use-jobs";
import { getJobPresentation, getReviewHref, isCompletedReviewJob } from "@/lib/job-presenter";
import { artifactService } from "@/services/artifact.service";
import { jobService } from "@/services/job.service";
import type { JobSummary } from "@/types/job";

export default function JobsPage() {
  const router = useRouter();
  const [workspaceId, setWorkspaceId] = useState("");
  const [openingJobId, setOpeningJobId] = useState<string | null>(null);
  const [navigationError, setNavigationError] = useState<string | null>(null);
  const jobs = useJobs({ workspaceId: workspaceId || undefined });
  const retryJob = useRetryJob();
  const groupedJobs = useMemo(() => {
    const groups = new Map<string, JobSummary[]>();
    for (const job of jobs.data ?? []) {
      const category = getJobPresentation(job.jobType).category;
      groups.set(category, [...(groups.get(category) ?? []), job]);
    }
    return Array.from(groups.entries());
  }, [jobs.data]);

  async function handleJobClick(jobId: string) {
    setNavigationError(null);
    setOpeningJobId(jobId);

    try {
      const job = await jobService.get(jobId);
      const reviewHref = getReviewHref(job);
      if (!reviewHref) {
        const latestReviewArtifacts = await artifactService.listByWorkspace(job.workspaceId, {
          artifactType: "SPRINT_REVIEW",
          referenceType: "SPRINT",
        });
        const latestArtifact = latestReviewArtifacts.artifacts
          .filter((artifact) => artifact.referenceType === "SPRINT")
          .sort((left, right) => {
            const leftTimestamp = left.generatedAt ?? left.createdAt;
            const rightTimestamp = right.generatedAt ?? right.createdAt;
            return new Date(rightTimestamp).getTime() - new Date(leftTimestamp).getTime();
          })[0];
        if (!latestArtifact?.referenceId) {
          setNavigationError("This review job completed, but it does not include enough sprint context to open the review directly.");
          return;
        }
        router.push(`/review/${job.workspaceId}/${latestArtifact.referenceId}`);
        return;
      }
      router.push(reviewHref);
    } catch (error) {
      setNavigationError(error instanceof Error ? error.message : "Unable to open the review for this completed job.");
    } finally {
      setOpeningJobId(null);
    }
  }

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

      <div className="grid gap-6">
        {jobs.isLoading ? <p className="text-sm text-stone-600">Loading jobs...</p> : null}
        {jobs.error ? <p className="text-sm text-rose-600">{jobs.error.message}</p> : null}
        {groupedJobs.map(([category, grouped]) => (
          <section key={category} className="space-y-4">
            <div>
              <p className="text-xs uppercase tracking-[0.18em] text-stone-500">{category}</p>
            </div>
            {grouped?.map((job) => {
              const presentation = getJobPresentation(job.jobType);
              const reviewJob = isCompletedReviewJob(job);
              const openingThisJob = openingJobId === job.id;
              return (
                <Card
                  key={job.id}
                  className={reviewJob ? "cursor-pointer transition hover:border-amber-400 hover:bg-amber-50/50" : undefined}
                  onClick={reviewJob ? () => void handleJobClick(job.id) : undefined}
                  onKeyDown={
                    reviewJob
                      ? (event) => {
                          if (event.key === "Enter" || event.key === " ") {
                            event.preventDefault();
                            void handleJobClick(job.id);
                          }
                        }
                      : undefined
                  }
                  role={reviewJob ? "button" : undefined}
                  tabIndex={reviewJob ? 0 : undefined}
                >
                  <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                    <div>
                      <div className="flex flex-wrap items-center gap-3">
                        <h2 className="text-lg font-semibold text-ink">{presentation.label}</h2>
                        <Badge tone={job.status === "COMPLETED" ? "success" : job.status === "FAILED" ? "danger" : "default"}>
                          {job.status}
                        </Badge>
                      </div>
                      <p className="mt-2 text-sm text-stone-600">{presentation.description}</p>
                      <p className="mt-2 text-sm text-stone-600">Workspace {job.workspaceId}</p>
                      <p className="mt-1 text-xs uppercase tracking-[0.16em] text-stone-500">
                        Attempts {job.attemptCount}/{job.maxAttempts} • Updated {new Date(job.updatedAt).toLocaleString()}
                      </p>
                    </div>
                    <div className="flex gap-2">
                      {reviewJob ? (
                        <Button
                          disabled={openingThisJob}
                          variant="secondary"
                          onClick={(event) => {
                            event.stopPropagation();
                            void handleJobClick(job.id);
                          }}
                        >
                          {openingThisJob ? "Opening review..." : presentation.completionLabel}
                        </Button>
                      ) : null}
                      {job.status === "FAILED" ? (
                        <Button
                          disabled={retryJob.isPending}
                          onClick={(event) => {
                            event.stopPropagation();
                            retryJob.mutate(job.id);
                          }}
                        >
                          Retry
                        </Button>
                      ) : null}
                    </div>
                  </div>
                </Card>
              );
            })}
          </section>
        ))}
      </div>
      {navigationError ? <p className="text-sm text-rose-600">{navigationError}</p> : null}
      {retryJob.error ? <p className="text-sm text-rose-600">{retryJob.error.message}</p> : null}
    </div>
  );
}
