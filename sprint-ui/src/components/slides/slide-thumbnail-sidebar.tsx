import { AddSlideMenu } from "@/components/slides/add-slide-menu";
import { ReorderableSlideList } from "@/components/slides/reorderable-slide-list";
import { SlideActionsMenu } from "@/components/slides/slide-actions-menu";
import type { AddSlideRequest, PresentationSlide } from "@/types/presentation";

export function SlideThumbnailSidebar({
  slides,
  selectedSlide,
  onSelectSlide,
  onAddSlide,
  onDuplicateSlide,
  onDeleteSlide,
  onReorderSlides,
}: {
  slides: PresentationSlide[];
  selectedSlide: PresentationSlide | null;
  onSelectSlide: (slideId: string) => void;
  onAddSlide: (payload: AddSlideRequest) => void;
  onDuplicateSlide: () => void;
  onDeleteSlide: () => void;
  onReorderSlides: (sourceSlideId: string, targetSlideId: string) => void;
}) {
  return (
    <aside className="flex h-full min-h-0 flex-col rounded-[28px] border border-line bg-[#eef2f6] p-4 shadow-panel">
      <div className="flex items-center justify-between gap-3 border-b border-line px-1 pb-3">
        <div>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Slides</p>
          <p className="mt-1 text-sm font-semibold text-ink">{slides.length} total</p>
        </div>
        <div className="text-xs text-stone-500">Drag to reorder</div>
      </div>

      <div className="mt-4 flex-1 overflow-y-auto pr-1">
        <ReorderableSlideList
          onReorder={onReorderSlides}
          onSelectSlide={onSelectSlide}
          selectedSlideId={selectedSlide?.id ?? null}
          slides={slides}
        />
      </div>

      <div className="mt-4 space-y-4 border-t border-line pt-4">
        <SlideActionsMenu
          canDelete={slides.length > 1}
          onDelete={onDeleteSlide}
          onDuplicate={onDuplicateSlide}
          slide={selectedSlide}
        />
        <AddSlideMenu onAdd={onAddSlide} />
      </div>
    </aside>
  );
}
