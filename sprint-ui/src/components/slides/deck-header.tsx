import { Input } from "@/components/ui/input";
import type { DeckStatus } from "@/types/presentation";

const deckStatuses: DeckStatus[] = ["DRAFT", "READY", "ARCHIVED"];

export function DeckHeader({
  title,
  subtitle,
  status,
  dirty,
  disabled,
  onTitleChange,
  onSubtitleChange,
  onStatusChange,
}: {
  title: string;
  subtitle?: string | null;
  status: DeckStatus;
  dirty: boolean;
  disabled?: boolean;
  onTitleChange: (value: string) => void;
  onSubtitleChange: (value: string) => void;
  onStatusChange: (value: DeckStatus) => void;
}) {
  return (
    <div className="rounded-[2rem] border border-line bg-white p-5">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Editable deck</p>
          <p className="mt-2 text-sm text-stone-600">
            {dirty ? "Unsaved edits" : "All changes saved"}
          </p>
        </div>
        <select
          className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          disabled={disabled}
          onChange={(event) => onStatusChange(event.target.value as DeckStatus)}
          value={status}
        >
          {deckStatuses.map((deckStatus) => (
            <option key={deckStatus} value={deckStatus}>
              {deckStatus}
            </option>
          ))}
        </select>
      </div>

      <div className="mt-5 grid gap-4 md:grid-cols-2">
        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Deck title</label>
          <Input disabled={disabled} onChange={(event) => onTitleChange(event.target.value)} value={title} />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Subtitle</label>
          <Input disabled={disabled} onChange={(event) => onSubtitleChange(event.target.value)} value={subtitle || ""} />
        </div>
      </div>
    </div>
  );
}
