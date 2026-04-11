import { Button } from "@/components/ui/button";

export function ItalicToggleButton({
  active,
  disabled,
  onToggle,
}: {
  active: boolean;
  disabled: boolean;
  onToggle: () => void;
}) {
  return (
    <Button
      aria-pressed={active}
      className={active ? "border-blue-500 bg-blue-50 text-blue-700" : undefined}
      disabled={disabled}
      onClick={onToggle}
      type="button"
      variant="secondary"
    >
      <span className="text-base italic">I</span>
    </Button>
  );
}
