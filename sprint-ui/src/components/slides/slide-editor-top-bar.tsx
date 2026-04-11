import Link from "next/link";

import { Input } from "@/components/ui/input";
import type { DeckStatus } from "@/types/presentation";

const deckStatuses: DeckStatus[] = ["DRAFT", "READY", "ARCHIVED"];

export function SlideEditorTopBar({
  title,
  subtitle,
  status,
  dirty,
  savePending,
  reviewHref,
  onTitleChange,
  onSubtitleChange,
  onStatusChange,
  onSave,
}: {
  title: string;
  subtitle?: string | null;
  status: DeckStatus;
  dirty: boolean;
  savePending?: boolean;
  reviewHref: string;
  onTitleChange: (value: string) => void;
  onSubtitleChange: (value: string) => void;
  onStatusChange: (value: DeckStatus) => void;
  onSave: () => void;
}) {
  return (
    <header className="border-b border-slate-200 bg-white/95 backdrop-blur">
      <div className="flex h-16 items-center gap-4 px-5">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-3">
            <span className="text-[11px] font-semibold uppercase tracking-[0.18em] text-slate-500">Presentation Editor</span>
            <span className="rounded-full border border-slate-200 bg-slate-50 px-2 py-0.5 text-[11px] font-medium text-slate-600">
              {dirty ? "Unsaved changes" : "All changes saved"}
            </span>
          </div>
          <div className="mt-2 grid gap-2 md:grid-cols-[minmax(0,1fr)_16rem]">
            <Input
              className="h-9 rounded-md border-slate-300 bg-white text-sm font-semibold text-slate-900 shadow-none"
              onChange={(event) => onTitleChange(event.target.value)}
              value={title}
            />
            <Input
              className="h-9 rounded-md border-slate-300 bg-white text-sm text-slate-600 shadow-none"
              onChange={(event) => onSubtitleChange(event.target.value)}
              placeholder="Subtitle"
              value={subtitle || ""}
            />
          </div>
        </div>

        <div className="flex items-center gap-2 border-l border-slate-200 pl-4">
          <select
            className="h-9 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-700 outline-none"
            onChange={(event) => onStatusChange(event.target.value as DeckStatus)}
            value={status}
          >
            {deckStatuses.map((deckStatus) => (
              <option key={deckStatus} value={deckStatus}>
                {deckStatus}
              </option>
            ))}
          </select>
          <button
            className="inline-flex h-9 items-center rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            onClick={onSave}
            type="button"
            disabled={!dirty || savePending}
          >
            {savePending ? "Saving..." : "Save"}
          </button>
          <button
            className="inline-flex h-9 items-center rounded-md border border-slate-200 bg-slate-100 px-3 text-sm font-medium text-slate-400"
            type="button"
            disabled
          >
            Export PPTX soon
          </button>
          <Link
            className="inline-flex h-9 items-center rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            href={reviewHref}
          >
            Exit editor
          </Link>
        </div>
      </div>
    </header>
  );
}
