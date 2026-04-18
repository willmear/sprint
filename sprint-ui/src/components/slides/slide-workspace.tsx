import { SlideCanvasStage } from "@/components/slides/slide-canvas-stage";
import type { PresentationSlide, PresentationThemeSummary } from "@/types/presentation";

export function SlideWorkspace({
  slide,
  deckTitle,
  deckSubtitle,
  deckTheme,
  selectedElementId,
  onSelectElement,
  onChangeElementText,
  onUpdateElementFrame,
}: {
  slide: PresentationSlide | null;
  deckTitle: string;
  deckSubtitle?: string | null;
  deckTheme?: PresentationThemeSummary | null;
  selectedElementId: string | null;
  onSelectElement: (elementId: string | null) => void;
  onChangeElementText: (elementId: string, text: string) => void;
  onUpdateElementFrame: (elementId: string, nextFrame: Pick<PresentationSlide["elements"][number], "x" | "y" | "width" | "height">) => void;
}) {
  return (
    <section className="flex min-h-0 flex-1 flex-col bg-[#eef0f3]">
      <div className="border-b border-slate-200 px-6 py-3 text-xs uppercase tracking-[0.14em] text-slate-500">
        Slide workspace
      </div>
      <div className="flex min-h-0 flex-1 items-center justify-center overflow-auto p-8">
        <SlideCanvasStage
          deckSubtitle={deckSubtitle}
          deckTheme={deckTheme}
          deckTitle={deckTitle}
          onChangeElementText={onChangeElementText}
          onSelectElement={onSelectElement}
          onUpdateElementFrame={onUpdateElementFrame}
          selectedElementId={selectedElementId}
          slide={slide}
        />
      </div>
    </section>
  );
}
