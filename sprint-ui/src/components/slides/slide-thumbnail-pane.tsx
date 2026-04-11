import { AddSlideMenu } from "@/components/slides/add-slide-menu";
import { ReorderableSlideList } from "@/components/slides/reorderable-slide-list";
import { SlideActionsMenu } from "@/components/slides/slide-actions-menu";
import type { AddSlideRequest, PresentationSlide } from "@/types/presentation";

export function SlideThumbnailPane({
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
    <aside className="flex h-full min-h-0 flex-col border-r border-slate-200 bg-[#f5f6f8]">
      <div className="border-b border-slate-200 px-4 py-3">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-500">Slides</p>
        <p className="mt-1 text-sm text-slate-600">{slides.length} slides</p>
      </div>

      <div className="flex-1 overflow-y-auto px-2 py-3">
        <ReorderableSlideList
          onReorder={onReorderSlides}
          onSelectSlide={onSelectSlide}
          selectedSlideId={selectedSlide?.id ?? null}
          slides={slides}
        />
      </div>

      <div className="space-y-3 border-t border-slate-200 bg-white px-3 py-3">
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
