"use client";

import type { PresentationSlide } from "@/types/presentation";
import { SlideCanvasElement } from "@/components/slides/slide-canvas-element";

export function SlideCanvasStage({
  slide,
  deckTitle,
  deckSubtitle,
  selectedElementId,
  onSelectElement,
  onChangeElementText,
  onUpdateElementFrame,
}: {
  slide: PresentationSlide | null;
  deckTitle: string;
  deckSubtitle?: string | null;
  selectedElementId: string | null;
  onSelectElement: (elementId: string | null) => void;
  onChangeElementText: (elementId: string, text: string) => void;
  onUpdateElementFrame: (elementId: string, nextFrame: Pick<PresentationSlide["elements"][number], "x" | "y" | "width" | "height">) => void;
}) {
  if (!slide) {
    return (
      <div className="flex aspect-[16/9] w-full max-w-[1080px] items-center justify-center rounded-sm border border-dashed border-slate-300 bg-white text-sm text-slate-500 shadow-sm">
        Select a slide from the left rail.
      </div>
    );
  }

  return (
    <div className="w-full max-w-[1080px]">
      <div className="mb-3 flex items-center justify-between px-1 text-xs text-slate-500">
        <div className="font-medium text-slate-600">Slide {slide.slideOrder + 1}</div>
        <div>{deckSubtitle || deckTitle}</div>
      </div>
      <div className="rounded-[18px] border border-slate-300/80 bg-white p-4 shadow-[0_18px_48px_rgba(15,23,42,0.14)]">
        <div
          className="relative aspect-[16/9] w-full overflow-hidden rounded-[4px] border border-slate-200 bg-white"
          data-slide-canvas="true"
          onClick={() => onSelectElement(null)}
          style={{
            backgroundImage:
              "linear-gradient(to right, rgba(226,232,240,0.35) 1px, transparent 1px), linear-gradient(to bottom, rgba(226,232,240,0.35) 1px, transparent 1px)",
            backgroundSize: "24px 24px",
          }}
        >
          {slide.elements.map((element) => (
            <SlideCanvasElement
              key={element.id}
              element={element}
              onChange={(text) => onChangeElementText(element.id, text)}
              onSelect={() => onSelectElement(element.id)}
              onUpdateFrame={onUpdateElementFrame}
              selected={selectedElementId === element.id}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
