import { useState } from "react";

import { SlideThumbnail } from "@/components/slides/slide-thumbnail";
import type { PresentationSlide } from "@/types/presentation";

export function ReorderableSlideList({
  slides,
  selectedSlideId,
  onSelectSlide,
  onReorder,
}: {
  slides: PresentationSlide[];
  selectedSlideId: string | null;
  onSelectSlide: (slideId: string) => void;
  onReorder: (sourceSlideId: string, targetSlideId: string) => void;
}) {
  const [draggingSlideId, setDraggingSlideId] = useState<string | null>(null);

  return (
    <div className="space-y-1.5">
      {slides.map((slide) => (
        <SlideThumbnail
          key={slide.id}
          draggable
          onDragEnd={() => setDraggingSlideId(null)}
          onDragStart={() => setDraggingSlideId(slide.id)}
          onDrop={() => {
            if (draggingSlideId && draggingSlideId !== slide.id) {
              onReorder(draggingSlideId, slide.id);
            }
            setDraggingSlideId(null);
          }}
          onSelect={() => onSelectSlide(slide.id)}
          selected={selectedSlideId === slide.id}
          slide={slide}
        />
      ))}
    </div>
  );
}
