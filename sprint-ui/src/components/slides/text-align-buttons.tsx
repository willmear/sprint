import { Button } from "@/components/ui/button";
import type { TextAlignment } from "@/types/presentation";

const options: Array<{ label: string; value: TextAlignment }> = [
  { label: "Left", value: "LEFT" },
  { label: "Center", value: "CENTER" },
  { label: "Right", value: "RIGHT" },
];

export function TextAlignButtons({
  disabled,
  value,
  onChange,
}: {
  disabled: boolean;
  value: TextAlignment | null;
  onChange: (value: TextAlignment) => void;
}) {
  return (
    <div className="flex items-center gap-2">
      <span className="text-xs uppercase tracking-[0.14em] text-stone-500">Align</span>
      <div className="flex items-center gap-2 rounded-2xl border border-line bg-cloud p-1">
        {options.map((option) => (
          <Button
            key={option.value}
            aria-pressed={value === option.value}
            className={value === option.value ? "border-blue-500 bg-white text-blue-700 shadow-sm" : "border-transparent bg-transparent"}
            disabled={disabled}
            onClick={() => onChange(option.value)}
            size="sm"
            type="button"
            variant="secondary"
          >
            {option.label}
          </Button>
        ))}
      </div>
    </div>
  );
}
