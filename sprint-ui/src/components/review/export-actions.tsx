"use client";

import { useMemo, useState } from "react";

import { copyToClipboard, downloadTextFile } from "@/lib/export-client";
import { useExportLatestSprintReview } from "@/lib/hooks/use-export";
import { PresentationOutlinePreview } from "@/components/review/presentation-outline-preview";
import { SpeakerNotesCopyButton } from "@/components/review/speaker-notes-copy-button";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import type { ExportFormat, ExportResponse, PresentationOutline } from "@/types/export";

export function ExportActions({
  workspaceId,
  sprintId,
  disabled,
}: {
  workspaceId: string;
  sprintId: string | number;
  disabled?: boolean;
}) {
  const exportLatestSprintReview = useExportLatestSprintReview(workspaceId, sprintId);
  const [previewExport, setPreviewExport] = useState<ExportResponse | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [activeFormat, setActiveFormat] = useState<ExportFormat | null>(null);

  async function handlePreview(format: Extract<ExportFormat, "MARKDOWN" | "PRESENTATION_OUTLINE">) {
    setFeedbackMessage(null);
    setActiveFormat(format);
    try {
      const exported = await exportLatestSprintReview.mutateAsync(format);
      setPreviewExport(exported);
      setFeedbackMessage(format === "MARKDOWN" ? "Markdown export is ready to preview, copy, or download." : "Presentation outline is ready to preview, copy, or download.");
    } catch (error) {
      setFeedbackMessage(error instanceof Error ? error.message : "Export failed.");
    } finally {
      setActiveFormat(null);
    }
  }

  async function handleCopySpeakerNotes() {
    setFeedbackMessage(null);
    setActiveFormat("SPEAKER_NOTES");
    try {
      const exported = await exportLatestSprintReview.mutateAsync("SPEAKER_NOTES");
      await copyToClipboard(exported.textContent ?? "");
      setPreviewExport(exported);
      setFeedbackMessage("Speaker notes copied to clipboard.");
    } catch (error) {
      setFeedbackMessage(error instanceof Error ? error.message : "Failed to copy speaker notes.");
    } finally {
      setActiveFormat(null);
    }
  }

  async function handleCopyPreview() {
    if (!previewExport?.textContent) {
      return;
    }
    await copyToClipboard(previewExport.textContent);
    setFeedbackMessage(`${formatLabel(previewExport.format)} copied to clipboard.`);
  }

  function handleDownloadPreview() {
    if (!previewExport) {
      return;
    }
    const content = resolveDownloadContent(previewExport);
    if (!content) {
      return;
    }
    downloadTextFile(previewExport.fileName, content, previewExport.contentType);
    setFeedbackMessage(`${formatLabel(previewExport.format)} downloaded.`);
  }

  const outlinePreview = useMemo(() => {
    if (previewExport?.format !== "PRESENTATION_OUTLINE" || !previewExport.structuredContent) {
      return null;
    }
    return normalizePresentationOutline(previewExport.structuredContent);
  }, [previewExport]);

  return (
    <Card>
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Export and share</p>
          <h2 className="mt-2 font-display text-2xl font-bold text-ink">One-click review export</h2>
          <p className="mt-2 max-w-2xl text-sm text-stone-600">
            Export the persisted review as markdown, prepare a presentation outline, or copy speaker notes for the live readout.
          </p>
        </div>
      </div>

      <div className="mt-6 flex flex-wrap gap-3">
        <Button disabled={disabled || exportLatestSprintReview.isPending} type="button" variant="secondary" onClick={() => void handlePreview("MARKDOWN")}>
          {activeFormat === "MARKDOWN" ? "Preparing markdown..." : "Export markdown"}
        </Button>
        <Button
          disabled={disabled || exportLatestSprintReview.isPending}
          type="button"
          variant="secondary"
          onClick={() => void handlePreview("PRESENTATION_OUTLINE")}
        >
          {activeFormat === "PRESENTATION_OUTLINE" ? "Preparing outline..." : "Export presentation outline"}
        </Button>
        <SpeakerNotesCopyButton
          disabled={disabled || exportLatestSprintReview.isPending}
          isPending={activeFormat === "SPEAKER_NOTES"}
          onClick={() => void handleCopySpeakerNotes()}
        />
      </div>

      {feedbackMessage ? (
        <p className="mt-4 text-sm text-stone-700">{feedbackMessage}</p>
      ) : null}
      {exportLatestSprintReview.error ? (
        <p className="mt-2 text-sm text-rose-600">{exportLatestSprintReview.error.message}</p>
      ) : null}

      {previewExport ? (
        <div className="mt-6 space-y-4 rounded-[2rem] border border-line bg-cloud p-5">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Export preview</p>
              <h3 className="mt-2 text-lg font-semibold text-ink">{formatLabel(previewExport.format)}</h3>
              <p className="mt-1 text-sm text-stone-600">Generated {new Date(previewExport.generatedAt).toLocaleString()}</p>
            </div>
            <div className="flex flex-wrap gap-2">
              {previewExport.textContent ? (
                <Button type="button" variant="secondary" onClick={() => void handleCopyPreview()}>
                  Copy
                </Button>
              ) : null}
              <Button type="button" variant="secondary" onClick={handleDownloadPreview}>
                Download
              </Button>
            </div>
          </div>

          {previewExport.format === "MARKDOWN" ? (
            <pre className="overflow-x-auto rounded-3xl border border-line bg-white p-4 text-sm text-stone-700 whitespace-pre-wrap">
              {previewExport.textContent}
            </pre>
          ) : null}

          {previewExport.format === "PRESENTATION_OUTLINE" && outlinePreview ? (
            <PresentationOutlinePreview outline={outlinePreview} />
          ) : null}
          {previewExport.format === "PRESENTATION_OUTLINE" && !outlinePreview ? (
            <div className="rounded-3xl border border-amber-200 bg-amber-50 p-4">
              <p className="text-sm text-amber-950">
                The outline export completed, but the preview payload was not in the expected shape. You can still copy or download the export.
              </p>
            </div>
          ) : null}

          {previewExport.format === "SPEAKER_NOTES" ? (
            <pre className="overflow-x-auto rounded-3xl border border-line bg-white p-4 text-sm text-stone-700 whitespace-pre-wrap">
              {previewExport.textContent}
            </pre>
          ) : null}
        </div>
      ) : null}
    </Card>
  );
}

function resolveDownloadContent(previewExport: ExportResponse) {
  if (previewExport.structuredContent && previewExport.format === "PRESENTATION_OUTLINE") {
    return JSON.stringify(previewExport.structuredContent, null, 2);
  }
  return previewExport.textContent ?? "";
}

function formatLabel(format: ExportFormat) {
  switch (format) {
    case "MARKDOWN":
      return "Markdown export";
    case "PRESENTATION_OUTLINE":
      return "Presentation outline";
    case "SPEAKER_NOTES":
      return "Speaker notes";
    case "POWERPOINT":
      return "PowerPoint export";
    default:
      return format;
  }
}

function normalizePresentationOutline(value: ExportResponse["structuredContent"]): PresentationOutline | null {
  if (!value || typeof value !== "object") {
    return null;
  }

  const candidate = "slides" in value ? value : "presentationOutline" in value && typeof value.presentationOutline === "object" ? value.presentationOutline : null;
  if (!candidate || !("slides" in candidate) || !Array.isArray(candidate.slides) || typeof candidate.title !== "string") {
    return null;
  }

  const slides = candidate.slides
    .filter((slide): slide is Record<string, unknown> => Boolean(slide) && typeof slide === "object")
    .map((slide) => ({
      slideNumber: typeof slide.slideNumber === "number" ? slide.slideNumber : 0,
      title: typeof slide.title === "string" ? slide.title : "Untitled slide",
      bulletPoints: Array.isArray(slide.bulletPoints) ? slide.bulletPoints.filter((point): point is string => typeof point === "string") : [],
      speakerNotes: typeof slide.speakerNotes === "string" ? slide.speakerNotes : null,
    }));

  return {
    title: candidate.title,
    slides,
  };
}
