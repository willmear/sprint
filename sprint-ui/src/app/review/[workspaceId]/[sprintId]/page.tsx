"use client";

import Link from "next/link";
import { FormEvent, use, useEffect, useMemo, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { ApiError } from "@/lib/api/client";
import { useJob } from "@/lib/hooks/use-jobs";
import { useJiraConnections } from "@/lib/hooks/use-jira";
import { useEnqueueSprintReviewJob, useSprintReview, useSprintReviewContext } from "@/lib/hooks/use-review";
import { useSyncSprint } from "@/lib/hooks/use-sprints";
import { ExportActions } from "@/components/review/export-actions";
import { ReviewDisplay } from "@/components/review/review-display";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";

type GenerationPhase = "idle" | "syncing" | "queueing" | "waiting" | "generating" | "completed" | "failed";

export default function ReviewPage({
  params,
}: {
  params: Promise<{ workspaceId: string; sprintId: string }>;
}) {
  const { workspaceId, sprintId } = use(params);
  const queryClient = useQueryClient();
  const connections = useJiraConnections(workspaceId);
  const activeConnection = connections.data?.find((connection) => connection.status === "ACTIVE") ?? connections.data?.[0];
  const review = useSprintReview(workspaceId, sprintId);
  const context = useSprintReviewContext(workspaceId, sprintId);
  const enqueueReviewJob = useEnqueueSprintReviewJob(workspaceId, sprintId);
  const syncSprint = useSyncSprint(workspaceId, activeConnection?.id);

  const [audience, setAudience] = useState("leadership");
  const [tone, setTone] = useState("concise");
  const [generationPhase, setGenerationPhase] = useState<GenerationPhase>("idle");
  const [generationMessage, setGenerationMessage] = useState<string | null>(null);
  const [activeJobId, setActiveJobId] = useState<string | null>(null);
  const [lastHandledTerminalJobId, setLastHandledTerminalJobId] = useState<string | null>(null);

  const activeJob = useJob(activeJobId ?? undefined, { enabled: Boolean(activeJobId), refetchInterval: 1500 });
  const hasNoReviewYet = review.error instanceof ApiError && review.error.status === 404;
  const hasUnexpectedReviewError = Boolean(review.error) && !hasNoReviewYet;
  const flowBusy = syncSprint.isPending || enqueueReviewJob.isPending || Boolean(activeJobId);

  useEffect(() => {
    if (!activeJob.data || !activeJobId) {
      return;
    }

    const job = activeJob.data;
    if (job.status === "PENDING") {
      setGenerationPhase("waiting");
      setGenerationMessage("Review generation is queued. Sprint Studio will update this page automatically when work starts.");
      return;
    }
    if (job.status === "RUNNING") {
      setGenerationPhase("generating");
      setGenerationMessage("Generating the sprint review. The backend is assembling and persisting the latest artifact now.");
      return;
    }
    if (job.status === "COMPLETED") {
      if (lastHandledTerminalJobId === job.id) {
        return;
      }
      setLastHandledTerminalJobId(job.id);
      setGenerationPhase("completed");
      setGenerationMessage("Sprint review generated successfully. Loading the latest persisted review.");
      setActiveJobId(null);
      void Promise.all([
        queryClient.invalidateQueries({ queryKey: ["jobs"] }),
        queryClient.invalidateQueries({ queryKey: ["review", workspaceId, sprintId] }),
        queryClient.invalidateQueries({ queryKey: ["artifacts", workspaceId, sprintId] }),
        review.refetch(),
      ]);
      return;
    }
    if (job.status === "FAILED" || job.status === "CANCELLED") {
      if (lastHandledTerminalJobId === job.id) {
        return;
      }
      setLastHandledTerminalJobId(job.id);
      setGenerationPhase("failed");
      setGenerationMessage(
        job.errorMessage ||
          "Review generation did not complete. Retry the flow to sync the sprint and queue a fresh generation job."
      );
      setActiveJobId(null);
    }
  }, [activeJob.data, activeJobId, lastHandledTerminalJobId, queryClient, review, sprintId, workspaceId]);

  async function handleGenerate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await runReviewFlow();
  }

  async function runReviewFlow() {
    if (!activeConnection?.id) {
      setGenerationPhase("failed");
      setGenerationMessage("No Jira workspace connection is available. Authorize Jira for this workspace before generating review.");
      return;
    }

    try {
      setLastHandledTerminalJobId(null);
      setGenerationPhase("syncing");
      setGenerationMessage("Refreshing sprint data from Jira before queueing the review generation job.");
      await syncSprint.mutateAsync(sprintId);

      setGenerationPhase("queueing");
      setGenerationMessage("Queueing review generation. Sprint Studio will keep checking until the persisted review is ready.");
      const queuedJob = await enqueueReviewJob.mutateAsync({
        includeComments: true,
        includeChangelog: true,
        forceRegenerate: true,
        audience,
        tone,
      });

      setActiveJobId(queuedJob.jobId);
      setGenerationPhase("waiting");
      setGenerationMessage("Review generation is queued and waiting for a worker.");
    } catch (error) {
      setActiveJobId(null);
      setGenerationPhase("failed");
      setGenerationMessage(error instanceof Error ? error.message : "Sprint review generation failed.");
    }
  }

  const progressCopy = useMemo(() => {
    return getProgressCopy(generationPhase, activeJob.data?.id ?? activeJobId);
  }, [activeJob.data?.id, activeJobId, generationPhase]);

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Sprint review</p>
          <h1 className="mt-2 font-display text-4xl font-bold text-ink">
            {review.data?.sprintName || context.data?.sprintName || `Sprint ${sprintId}`}
          </h1>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">
            Sync the sprint, queue the review generation job, and watch the persisted review appear here when the backend finishes.
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <Link href={`/workspaces/${workspaceId}/sprints/${sprintId}/slides`}>
            <Button type="button" variant="secondary">Edit slides</Button>
          </Link>
        </div>
      </header>

      <div className="grid gap-6 xl:grid-cols-[0.78fr_1.22fr]">
        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Sync and generate</h2>
          <p className="mt-2 text-sm text-stone-600">
            Refresh the sprint from Jira first, then queue review generation with audience and tone guidance.
          </p>

          <form className="mt-6 space-y-4" onSubmit={handleGenerate}>
            <div className="space-y-2">
              <label className="text-sm font-medium text-stone-700" htmlFor="audience">
                Audience
              </label>
              <Input id="audience" value={audience} onChange={(event) => setAudience(event.target.value)} />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-stone-700" htmlFor="tone">
                Tone
              </label>
              <Input id="tone" value={tone} onChange={(event) => setTone(event.target.value)} />
            </div>
            <div className="flex flex-wrap gap-3">
              <Button disabled={flowBusy || connections.isLoading} type="submit">
                {buttonLabel(generationPhase)}
              </Button>
              {generationPhase === "failed" ? (
                <Button disabled={flowBusy} type="button" variant="secondary" onClick={() => void runReviewFlow()}>
                  Retry
                </Button>
              ) : null}
            </div>
          </form>

          {connections.isLoading ? <p className="mt-4 text-sm text-stone-600">Loading Jira connection...</p> : null}
          {!activeConnection && !connections.isLoading ? (
            <p className="mt-4 text-sm text-rose-600">
              No Jira workspace connection found. Authorize Jira for this workspace before generating review.
            </p>
          ) : null}
          {generationMessage ? (
            <p className={generationPhase === "failed" ? "mt-4 text-sm text-rose-600" : "mt-4 text-sm text-stone-700"}>
              {generationMessage}
            </p>
          ) : null}

          {progressCopy ? (
            <div className="mt-4 rounded-3xl border border-amber-200 bg-amber-50 p-4">
              <div className="flex items-center gap-3">
                <div className="flex gap-2">
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500 [animation-delay:-0.3s]" />
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500 [animation-delay:-0.15s]" />
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-amber-950">{progressCopy.title}</p>
                  <p className="mt-1 text-sm text-amber-800">{progressCopy.description}</p>
                </div>
              </div>
            </div>
          ) : null}

          {activeJob.data ? (
            <div className="mt-4 rounded-3xl border border-line bg-cloud p-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Review job</p>
                  <p className="mt-2 text-sm text-stone-700">Job {activeJob.data.id}</p>
                </div>
                <Badge>{activeJob.data.status}</Badge>
              </div>
              <div className="mt-4">
                <Link href={`/jobs?workspaceId=${workspaceId}`}>
                  <Button variant="secondary">Open jobs</Button>
                </Link>
              </div>
            </div>
          ) : null}

          <div className="mt-8 space-y-4">
            <div className="rounded-3xl border border-line bg-cloud p-4">
              <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Prompt readiness</p>
              <p className="mt-2 text-sm text-stone-700">
                {context.data
                  ? `${context.data.totalIssueCount ?? context.data.allIssues.length} issues, ${context.data.totalCommentCount ?? 0} comments, ${context.data.totalChangelogCount ?? 0} changelog events available.`
                  : "Loading prompt context..."}
              </p>
            </div>
            <div className="rounded-3xl border border-line bg-cloud p-4">
              <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Connection</p>
              <p className="mt-2 text-sm text-stone-700">
                {activeConnection
                  ? `Using ${activeConnection.externalAccountDisplayName || activeConnection.baseUrl} for workspace Jira sync.`
                  : "No active Jira workspace connection selected."}
              </p>
            </div>
          </div>
        </Card>

        <div className="space-y-6">
          {review.data ? (
            <>
              <Card className="bg-cloud">
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div>
                    <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Generation overview</p>
                    <h2 className="mt-2 font-display text-2xl font-bold text-ink">Latest review snapshot</h2>
                    <p className="mt-2 text-sm text-stone-600">
                      Generated {new Date(review.data.generatedAt).toLocaleString()} from a{" "}
                      {review.data.generationSource.toLowerCase()} run.
                    </p>
                    {flowBusy ? (
                      <p className="mt-2 text-sm text-stone-700">
                        A newer review is currently in progress. This snapshot stays visible until the next persisted artifact is ready.
                      </p>
                    ) : null}
                  </div>
                  <Badge>{review.data.status}</Badge>
                </div>
                <div className="mt-6 grid gap-4 md:grid-cols-4">
                  <OverviewStat label="Themes" value={review.data.themes.length} />
                  <OverviewStat label="Highlights" value={review.data.highlights.length} />
                  <OverviewStat label="Blockers" value={review.data.blockers.length} />
                  <OverviewStat label="Speaker notes" value={review.data.speakerNotes.length} />
                </div>
              </Card>
              <ExportActions workspaceId={workspaceId} sprintId={sprintId} disabled={flowBusy} />
              <ReviewDisplay review={review.data} />
            </>
          ) : null}

          {review.isLoading ? <p className="text-sm text-stone-600">Loading latest review...</p> : null}

          {hasNoReviewYet ? (
            <Card>
              <p className="font-semibold text-ink">
                {flowBusy ? "Generating your first review" : "No persisted review yet"}
              </p>
              <p className="mt-2 text-sm text-stone-600">
                {flowBusy
                  ? "Sprint Studio is working through the sync and review job flow. This page will switch to the persisted review automatically when it completes."
                  : "Run the guided flow on the left to sync the sprint and queue review generation."}
              </p>
            </Card>
          ) : null}

          {hasUnexpectedReviewError ? (
            <Card>
              <p className="text-sm text-rose-600">
                {review.error instanceof Error ? review.error.message : "Failed to load the latest sprint review."}
              </p>
            </Card>
          ) : null}
        </div>
      </div>
    </div>
  );
}

function OverviewStat({ label, value }: { label: string; value: number }) {
  return (
    <div className="rounded-3xl border border-line bg-white p-4">
      <p className="text-xs uppercase tracking-[0.16em] text-stone-500">{label}</p>
      <p className="mt-2 text-2xl font-semibold text-ink">{value}</p>
    </div>
  );
}

function buttonLabel(phase: GenerationPhase) {
  switch (phase) {
    case "syncing":
      return "Syncing sprint...";
    case "queueing":
      return "Queueing review...";
    case "waiting":
      return "Waiting in queue...";
    case "generating":
      return "Generating review...";
    default:
      return "Sync sprint and generate review";
  }
}

function getProgressCopy(phase: GenerationPhase, jobId?: string | null) {
  switch (phase) {
    case "syncing":
      return {
        title: "Syncing sprint",
        description: "Refreshing the latest Jira issues, comments, and changelog before generation starts.",
      };
    case "queueing":
      return {
        title: "Queueing review generation",
        description: "Submitting the review job to the backend so it can be tracked and retried safely.",
      };
    case "waiting":
      return {
        title: "Waiting in queue",
        description: jobId
          ? `Job ${jobId} is queued and waiting for a worker to pick it up.`
          : "The backend has accepted the review job and is waiting to start work.",
      };
    case "generating":
      return {
        title: "Generating review",
        description: "The worker is assembling the sprint context and persisting the latest review artifact.",
      };
    default:
      return null;
  }
}
