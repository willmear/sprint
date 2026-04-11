import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import type { PresentationSlide, PresentationSlideElement, SlideLayoutType, SlideType } from "@/types/presentation";

const slideTypes: SlideType[] = ["TITLE", "OVERVIEW", "THEMES", "HIGHLIGHTS", "BLOCKERS", "SPEAKER_NOTES", "CUSTOM"];
const layoutTypes: SlideLayoutType[] = ["TITLE_ONLY", "TITLE_AND_BULLETS", "TITLE_BODY_NOTES", "SECTION_SUMMARY"];

export function SlidePropertiesPanel({
  slide,
  selectedElement,
  disabled,
  onSlideChange,
  onElementChange,
}: {
  slide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  disabled?: boolean;
  onSlideChange: (updater: (slide: PresentationSlide) => PresentationSlide) => void;
  onElementChange: (patch: Partial<PresentationSlideElement>) => void;
}) {
  if (!slide) {
    return (
      <div className="rounded-3xl border border-line bg-white p-5">
        <p className="text-sm text-stone-600">Choose a slide to edit its properties.</p>
      </div>
    );
  }

  return (
    <div className="space-y-5 rounded-3xl border border-line bg-white p-5">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Slide settings</p>
        <p className="mt-1 text-sm text-stone-600">Keep the overall slide structure aligned with the sprint review narrative.</p>
      </div>

      <div className="space-y-2">
        <label className="text-sm font-medium text-stone-700">Slide title summary</label>
        <Input disabled value={slide.title} />
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-1">
        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Slide type</label>
          <select
            className="w-full rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
            disabled={disabled}
            onChange={(event) => onSlideChange((current) => ({ ...current, slideType: event.target.value as SlideType }))}
            value={slide.slideType}
          >
            {slideTypes.map((type) => (
              <option key={type} value={type}>{type.replaceAll("_", " ")}</option>
            ))}
          </select>
        </div>

        <div className="space-y-2">
          <label className="text-sm font-medium text-stone-700">Layout</label>
          <select
            className="w-full rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
            disabled={disabled}
            onChange={(event) => onSlideChange((current) => ({ ...current, layoutType: event.target.value as SlideLayoutType }))}
            value={slide.layoutType}
          >
            {layoutTypes.map((type) => (
              <option key={type} value={type}>{type.replaceAll("_", " ")}</option>
            ))}
          </select>
        </div>
      </div>

      <label className="flex items-center gap-3 text-sm text-stone-700">
        <input
          checked={slide.hidden}
          disabled={disabled}
          onChange={(event) => onSlideChange((current) => ({ ...current, hidden: event.target.checked }))}
          type="checkbox"
        />
        Hide this slide from the final deck
      </label>

      <div className="space-y-2">
        <label className="text-sm font-medium text-stone-700">Speaker notes summary</label>
        <Textarea disabled value={slide.speakerNotes || ""} />
      </div>

      <div className="border-t border-line pt-5">
        <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Selected text box</p>
        {!selectedElement ? (
          <p className="mt-2 text-sm text-stone-600">Select a text box on the canvas to edit its position and content settings.</p>
        ) : (
          <div className="mt-4 space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-stone-700">Content</label>
              <Textarea
                className="min-h-28"
                disabled={disabled}
                onChange={(event) => onElementChange({ textContent: event.target.value })}
                value={selectedElement.textContent}
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <FieldNumber disabled={disabled} label="X" onChange={(value) => onElementChange({ x: value })} value={selectedElement.x} />
              <FieldNumber disabled={disabled} label="Y" onChange={(value) => onElementChange({ y: value })} value={selectedElement.y} />
              <FieldNumber disabled={disabled} label="Width" onChange={(value) => onElementChange({ width: value })} value={selectedElement.width} />
              <FieldNumber disabled={disabled} label="Height" onChange={(value) => onElementChange({ height: value })} value={selectedElement.height} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function FieldNumber({
  label,
  value,
  disabled,
  onChange,
}: {
  label: string;
  value: number;
  disabled?: boolean;
  onChange: (value: number) => void;
}) {
  return (
    <div className="space-y-2">
      <label className="text-sm font-medium text-stone-700">{label}</label>
      <Input disabled={disabled} onChange={(event) => onChange(Number(event.target.value) || 0)} type="number" value={Math.round(value)} />
    </div>
  );
}
