import { Textarea } from "@/components/ui/textarea";
import type { PresentationSlide } from "@/types/presentation";

export function SlideNotesEditor({
  slide,
  disabled,
  onUpdate,
}: {
  slide: PresentationSlide | null;
  disabled?: boolean;
  onUpdate: (updater: (slide: PresentationSlide) => PresentationSlide) => void;
}) {
  return (
    <div className="rounded-3xl border border-line bg-white p-5">
      <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Speaker notes</p>
      <p className="mt-1 text-sm text-stone-600">Keep presenter notes copy-ready for the final review readout.</p>
      <Textarea
        className="mt-4 min-h-48"
        disabled={disabled || !slide}
        onChange={(event) => onUpdate((current) => ({ ...current, speakerNotes: event.target.value || null }))}
        placeholder="Add talking points, transitions, or follow-up notes for this slide."
        value={slide?.speakerNotes || ""}
      />
    </div>
  );
}
