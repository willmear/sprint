import { cn } from "@/lib/utils/cn";
import type { PresentationSlide } from "@/types/presentation";

export function SlideThumbnailItem({
  slide,
  selected,
  onSelect,
  draggable,
  onDragStart,
  onDragOver,
  onDragEnd,
  onDrop,
}: {
  slide: PresentationSlide;
  selected: boolean;
  onSelect: () => void;
  draggable?: boolean;
  onDragStart?: () => void;
  onDragOver?: () => void;
  onDragEnd?: () => void;
  onDrop?: () => void;
}) {
  return (
    <button
      className={cn(
        "w-full rounded-3xl border p-3 text-left transition",
        selected ? "border-ink bg-white shadow-panel" : "border-line bg-cloud hover:bg-white"
      )}
      draggable={draggable}
      onClick={onSelect}
      onDragEnd={onDragEnd}
      onDragOver={(event) => {
        event.preventDefault();
        onDragOver?.();
      }}
      onDragStart={onDragStart}
      onDrop={(event) => {
        event.preventDefault();
        onDrop?.();
      }}
      type="button"
    >
      <p className="text-[11px] uppercase tracking-[0.16em] text-stone-500">Slide {slide.slideOrder + 1}</p>
      <div className="mt-3 rounded-2xl border border-line bg-white px-3 py-4">
        <p className="line-clamp-2 text-sm font-semibold text-ink">{slide.title}</p>
        <p className="mt-2 line-clamp-3 text-xs text-stone-600">{slide.bulletPoints.slice(0, 3).join(" • ") || slide.bodyText || "No content yet"}</p>
      </div>
    </button>
  );
}
