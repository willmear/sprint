import type { PresentationSlideElement } from "@/types/presentation";

const FONT_OPTIONS = ["Aptos", "Inter", "Georgia", "Times New Roman", "Helvetica"];

export function FontFamilySelect({
  disabled,
  value,
  onChange,
}: {
  disabled: boolean;
  value: PresentationSlideElement["fontFamily"] | "";
  onChange: (value: string) => void;
}) {
  return (
    <label className="flex items-center gap-2 text-sm text-stone-600">
      <span className="text-xs uppercase tracking-[0.14em] text-stone-500">Font</span>
      <select
        className="min-w-[10rem] rounded-2xl border border-line bg-cloud px-3 py-2 text-sm text-ink outline-none transition focus:border-blue-400 disabled:cursor-not-allowed disabled:opacity-60"
        disabled={disabled}
        onChange={(event) => onChange(event.target.value)}
        value={value || FONT_OPTIONS[0]}
      >
        {FONT_OPTIONS.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </label>
  );
}
