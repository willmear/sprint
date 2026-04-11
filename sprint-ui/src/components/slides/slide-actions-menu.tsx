import { Button } from "@/components/ui/button";
import type { PresentationSlide } from "@/types/presentation";

export function SlideActionsMenu({
  disabled,
  canDelete,
  slide,
  onDuplicate,
  onDelete,
}: {
  disabled?: boolean;
  canDelete: boolean;
  slide: PresentationSlide | null;
  onDuplicate: () => void;
  onDelete: () => void;
}) {
  return (
    <div className="rounded-3xl border border-line bg-white p-4">
      <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Slide actions</p>
      <p className="mt-2 text-sm font-semibold text-ink">{slide?.title || "Select a slide"}</p>
      <div className="mt-3 grid gap-2">
        <Button disabled={disabled || !slide} onClick={onDuplicate} type="button" variant="secondary">
          Duplicate slide
        </Button>
        <Button disabled={disabled || !slide || !canDelete} onClick={onDelete} type="button" variant="secondary">
          Delete slide
        </Button>
      </div>
    </div>
  );
}
