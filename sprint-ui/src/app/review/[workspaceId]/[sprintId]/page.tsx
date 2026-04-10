"use client";

import { FormEvent, useState } from "react";
import { use } from "react";

import { ReviewDisplay } from "@/components/review/review-display";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useJiraConnections } from "@/lib/hooks/use-jira";
import { useGenerateSprintReview, useSprintReview, useSprintReviewContext } from "@/lib/hooks/use-review";
import { useSyncSprint } from "@/lib/hooks/use-sprints";

type GenerationPhase = "idle" | "syncing" | "generating" | "succeeded" | "failed";

export default function ReviewPage({
  params,
}: {
  params: Promise<{ workspaceId: string; sprintId: string }>;
}) {
  const { workspaceId, sprintId } = use(params);
  const connections = useJiraConnections(workspaceId);
  const activeConnection = connections.data?.find((connection) => connection.status === "ACTIVE") ?? connections.data?.[0];
  const review = useSprintReview(workspaceId, sprintId);
  const context = useSprintReviewContext(workspaceId, sprintId);
  const generateReview = useGenerateSprintReview(workspaceId, sprintId);
  const syncSprint = useSyncSprint(workspaceId, activeConnection?.id);
  const [audience, setAudience] = useState("leadership");
  const [tone, setTone] = useState("concise");
  const [generationPhase, setGenerationPhase] = useState<GenerationPhase>("idle");
  const [generationMessage, setGenerationMessage] = useState<string | null>(null);

  async function handleGenerate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await runReviewFlow();
  }

  async function runReviewFlow() {
    if (!activeConnection?.id) {
      setGenerationPhase("failed");
      setGenerationMessage("No Jira connection is available for this workspace. Connect Jira before generating a review.");
      return;
    }

    try {
      setGenerationPhase("syncing");
      setGenerationMessage("Refreshing sprint data from Jira before generation.");
      await syncSprint.mutateAsync(sprintId);

      setGenerationPhase("generating");
      setGenerationMessage("Generating the sprint review. This can take a little time while the backend assembles the final artifact.");
      await generateReview.mutateAsync({
        includeComments: true,
        includeChangelog: true,
        forceRegenerate: true,
        audience,
        tone,
      });

      setGenerationPhase("succeeded");
      setGenerationMessage("Sprint review generated successfully.");
    } catch (error) {
      setGenerationPhase("failed");
      setGenerationMessage(error instanceof Error ? error.message : "Sprint review generation failed.");
    }
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Sprint review</p>
          <h1 className="mt-2 font-display text-4xl font-bold text-ink">{review.data?.sprintName || context.data?.sprintName || `Sprint ${sprintId}`}</h1>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">
            Sync the sprint, generate the structured review artifact, and inspect the final output in one flow.
          </p>
        </div>
      </header>

      <div className="grid gap-6 xl:grid-cols-[0.78fr_1.22fr]">
        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Sync and generate</h2>
          <p className="mt-2 text-sm text-stone-600">Refresh the sprint from Jira first, then generate the review with audience and tone guidance.</p>

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
              <Button disabled={generationPhase === "syncing" || generationPhase === "generating" || connections.isLoading} type="submit">
                {generationPhase === "syncing"
                  ? "Syncing sprint..."
                  : generationPhase === "generating"
                    ? "Generating review..."
                    : "Sync sprint and generate review"}
              </Button>
              {generationPhase === "failed" ? (
                <Button type="button" variant="secondary" onClick={runReviewFlow}>
                  Retry
                </Button>
              ) : null}
            </div>
          </form>

          {connections.isLoading ? <p className="mt-4 text-sm text-stone-600">Loading Jira connection...</p> : null}
          {!activeConnection && !connections.isLoading ? (
            <p className="mt-4 text-sm text-rose-600">No Jira connection found for this workspace. Connect Jira before generating a review.</p>
          ) : null}
          {generationMessage ? (
            <p className={generationPhase === "failed" ? "mt-4 text-sm text-rose-600" : "mt-4 text-sm text-stone-700"}>{generationMessage}</p>
          ) : null}

          {generationPhase === "syncing" || generationPhase === "generating" ? (
            <div className="mt-4 rounded-3xl border border-amber-200 bg-amber-50 p-4">
              <div className="flex items-center gap-3">
                <div className="flex gap-2">
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500 [animation-delay:-0.3s]" />
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500 [animation-delay:-0.15s]" />
                  <span className="h-2.5 w-2.5 animate-bounce rounded-full bg-amber-500" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-amber-950">
                    {generationPhase === "syncing" ? "Syncing sprint data" : "Generating sprint review"}
                  </p>
                  <p className="mt-1 text-sm text-amber-800">
                    {generationPhase === "syncing"
                      ? "Pulling the latest Jira issues, comments, and changelog into the local store."
                      : "Waiting for the backend to produce the final review artifact."}
                  </p>
                </div>
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
                  ? `Using ${activeConnection.externalAccountDisplayName || activeConnection.baseUrl} for the Jira sync step.`
                  : "No active Jira connection selected."}
              </p>
            </div>
          </div>
        </Card>

        <div className="space-y-6">
          {review.data ? (
            <Card className="bg-cloud">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Generation overview</p>
                  <h2 className="mt-2 font-display text-2xl font-bold text-ink">Latest review snapshot</h2>
                  <p className="mt-2 text-sm text-stone-600">
                    Generated {new Date(review.data.generatedAt).toLocaleString()} from a {review.data.generationSource.toLowerCase()} run.
                  </p>
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
          ) : null}
          {review.isLoading ? <p className="text-sm text-stone-600">Loading latest review...</p> : null}
          {review.error && generationPhase !== "failed" ? (
            <Card>
              <p className="text-sm text-stone-700">No generated review is available yet. Run the sync and generate flow to create one.</p>
            </Card>
          ) : null}
          {review.data ? <ReviewDisplay review={review.data} /> : null}
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
