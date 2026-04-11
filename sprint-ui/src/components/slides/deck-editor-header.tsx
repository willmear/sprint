import Link from "next/link";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import type { DeckStatus } from "@/types/presentation";

const deckStatuses: DeckStatus[] = ["DRAFT", "READY", "ARCHIVED"];

export function DeckEditorHeader({
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
    <header className="rounded-[28px] border border-line bg-white px-6 py-5 shadow-panel">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.22em] text-stone-500">Presentation editor</p>
          <p className="mt-2 text-sm text-stone-600">
            {dirty ? "Unsaved changes" : "All changes saved"}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <Badge>{status}</Badge>
          <select
            className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
            onChange={(event) => onStatusChange(event.target.value as DeckStatus)}
            value={status}
          >
            {deckStatuses.map((deckStatus) => (
              <option key={deckStatus} value={deckStatus}>{deckStatus}</option>
            ))}
          </select>
          <Button disabled={!dirty || savePending} onClick={onSave} type="button">
            {savePending ? "Saving..." : "Save deck"}
          </Button>
          <Link href={reviewHref}>
            <Button type="button" variant="secondary">Back to review</Button>
          </Link>
        </div>
      </div>

      <div className="mt-5 grid gap-4 lg:grid-cols-[minmax(0,1fr)_22rem]">
        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Deck title</label>
          <Input onChange={(event) => onTitleChange(event.target.value)} value={title} />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Subtitle</label>
          <Input onChange={(event) => onSubtitleChange(event.target.value)} value={subtitle || ""} />
        </div>
      </div>
    </header>
  );
}
