import { Card } from "@/components/ui/card";
import type { PresentationOutline } from "@/types/export";

export function PresentationOutlinePreview({ outline }: { outline: PresentationOutline }) {
  const slides = Array.isArray(outline.slides) ? outline.slides : [];

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Presentation outline preview</p>
        <h3 className="mt-2 font-display text-2xl font-bold text-ink">{outline.title}</h3>
      </div>
      <div className="grid gap-4">
        {slides.map((slide) => (
          <Card key={`${slide.slideNumber}-${slide.title}`} className="bg-white">
            <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Slide {slide.slideNumber}</p>
            <h4 className="mt-2 text-lg font-semibold text-ink">{slide.title}</h4>
            <ul className="mt-4 space-y-2 text-sm text-stone-700">
              {slide.bulletPoints.map((point) => (
                <li key={point} className="flex gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-ink" />
                  <span>{point}</span>
                </li>
              ))}
            </ul>
            {slide.speakerNotes ? (
              <div className="mt-4 rounded-3xl border border-amber-200 bg-amber-50 p-4">
                <p className="text-xs uppercase tracking-[0.18em] text-amber-800">Speaker notes</p>
                <p className="mt-2 text-sm text-amber-950">{slide.speakerNotes}</p>
              </div>
            ) : null}
          </Card>
        ))}
        {slides.length === 0 ? (
          <Card className="bg-white">
            <p className="text-sm text-stone-600">No slide outline content was available for preview.</p>
          </Card>
        ) : null}
      </div>
    </div>
  );
}
