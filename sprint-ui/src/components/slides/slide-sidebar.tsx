import { AddSlideMenu } from "@/components/slides/add-slide-menu";
import { ReorderableSlideList } from "@/components/slides/reorderable-slide-list";
import type { AddSlideRequest, PresentationSlide } from "@/types/presentation";

export function SlideSidebar({
  slides,
  selectedSlideId,
  disabled,
  onSelectSlide,
  onAddSlide,
  onReorderSlides,
}: {
  slides: PresentationSlide[];
  selectedSlideId: string | null;
  disabled?: boolean;
  onSelectSlide: (slideId: string) => void;
  onAddSlide: (payload: AddSlideRequest) => void;
  onReorderSlides: (sourceSlideId: string, targetSlideId: string) => void;
}) {
  return (
    <aside className="space-y-4">
      <AddSlideMenu disabled={disabled} onAdd={onAddSlide} />
      <div className="rounded-3xl border border-line bg-white p-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Slides</p>
            <p className="mt-1 text-sm font-semibold text-ink">{slides.length} total</p>
          </div>
          <p className="text-xs text-stone-500">Drag to reorder</p>
        </div>
        <div className="mt-4">
          <ReorderableSlideList
            onReorder={onReorderSlides}
            onSelectSlide={onSelectSlide}
            selectedSlideId={selectedSlideId}
            slides={slides}
          />
        </div>
      </div>
    </aside>
  );
}
