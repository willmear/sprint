import { Textarea } from "@/components/ui/textarea";
import type { PresentationSlide, PresentationSlideElement, PresentationThemeSummary } from "@/types/presentation";

function InspectorField({ label, value }: { label: string; value: string }) {
  return (
    <div className="space-y-1">
      <div className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">{label}</div>
      <div className="rounded-md border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">{value}</div>
    </div>
  );
}

export function SlideInspectorPanel({
  slide,
  selectedElement,
  deckTheme,
  onUpdateNotes,
  onSlideStyleChange,
  onSelectedElementChange,
}: {
  slide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  deckTheme?: PresentationThemeSummary | null;
  onUpdateNotes: (notes: string) => void;
  onSlideStyleChange: (updates: Partial<Pick<PresentationSlide, "backgroundColor" | "showGrid">>) => void;
  onSelectedElementChange: (
    updates: Partial<
      Pick<PresentationSlideElement, "textContent" | "x" | "y" | "width" | "height" | "fillColor" | "borderColor" | "borderWidth" | "textColor">
    >
  ) => void;
}) {
  const suggestedBackground = deckTheme?.colorPalette.background || "#ffffff";

  return (
    <aside className="flex h-full min-h-0 flex-col border-l border-slate-200 bg-white">
      <div className="border-b border-slate-200 px-4 py-3">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-500">Inspector</p>
        <p className="mt-1 text-sm text-slate-600">Slide details, appearance, and presenter notes</p>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        <section className="space-y-3">
          <h3 className="text-sm font-semibold text-slate-900">Current slide</h3>
          <InspectorField label="Title" value={slide?.title || "No slide selected"} />
          <InspectorField label="Type" value={slide?.slideType?.replaceAll("_", " ") || "-"} />
          <InspectorField label="Layout" value={slide?.layoutType?.replaceAll("_", " ") || "-"} />
        </section>

        <section className="space-y-3 border-t border-slate-200 pt-4">
          <h3 className="text-sm font-semibold text-slate-900">Slide appearance</h3>
          <label className="space-y-1 text-sm text-slate-700">
            <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Background</span>
            <div className="flex items-center gap-3">
              <input
                className="h-10 w-14 rounded border border-slate-200 bg-white"
                disabled={!slide}
                onChange={(event) => onSlideStyleChange({ backgroundColor: event.target.value })}
                type="color"
                value={slide?.backgroundColor || suggestedBackground}
              />
              <span className="text-xs text-slate-500">{slide?.backgroundColor || suggestedBackground}</span>
            </div>
          </label>
          <label className="flex items-center gap-3 text-sm text-slate-700">
            <input
              checked={slide?.showGrid ?? true}
              disabled={!slide}
              onChange={(event) => onSlideStyleChange({ showGrid: event.target.checked })}
              type="checkbox"
            />
            Show layout grid on slide
          </label>
        </section>

        <section className="space-y-3 border-t border-slate-200 pt-4">
          <h3 className="text-sm font-semibold text-slate-900">Selected object</h3>
          {!selectedElement ? (
            <p className="text-sm text-slate-500">Select a text box or shape on the canvas to inspect its frame and style.</p>
          ) : (
            <div className="space-y-3">
              <InspectorField label="Kind" value={selectedElement.elementType === "SHAPE" ? `Shape${selectedElement.shapeType ? ` • ${selectedElement.shapeType}` : ""}` : "Text box"} />
              <div className="grid grid-cols-2 gap-3">
                <NumberField label="X" onChange={(value) => onSelectedElementChange({ x: value })} value={selectedElement.x} />
                <NumberField label="Y" onChange={(value) => onSelectedElementChange({ y: value })} value={selectedElement.y} />
                <NumberField label="Width" onChange={(value) => onSelectedElementChange({ width: value })} value={selectedElement.width} />
                <NumberField label="Height" onChange={(value) => onSelectedElementChange({ height: value })} value={selectedElement.height} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <ColorField label="Fill" onChange={(value) => onSelectedElementChange({ fillColor: value })} value={selectedElement.fillColor || "#ffffff"} />
                <ColorField label="Border" onChange={(value) => onSelectedElementChange({ borderColor: value })} value={selectedElement.borderColor || "#2563eb"} />
                <ColorField label="Text" onChange={(value) => onSelectedElementChange({ textColor: value })} value={selectedElement.textColor || "#0f172a"} />
                <NumberField label="Border" onChange={(value) => onSelectedElementChange({ borderWidth: value })} value={selectedElement.borderWidth ?? 2} />
              </div>
              <div className="space-y-1">
                <div className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Content</div>
                <Textarea
                  className="min-h-28 rounded-md border-slate-300 bg-slate-50 px-3 py-2 text-sm shadow-none"
                  onChange={(event) => onSelectedElementChange({ textContent: event.target.value })}
                  value={selectedElement.textContent || ""}
                />
              </div>
            </div>
          )}
        </section>

        <section className="space-y-3 border-t border-slate-200 pt-4">
          <h3 className="text-sm font-semibold text-slate-900">Speaker notes</h3>
          <Textarea
            className="min-h-48 rounded-md border-slate-300 bg-slate-50 px-3 py-2 text-sm shadow-none"
            disabled={!slide}
            onChange={(event) => onUpdateNotes(event.target.value)}
            placeholder="Add talking points, transitions, or presenter reminders."
            value={slide?.speakerNotes || ""}
          />
        </section>
      </div>
    </aside>
  );
}

function NumberField({
  label,
  value,
  onChange,
}: {
  label: string;
  value: number;
  onChange: (value: number) => void;
}) {
  return (
    <label className="space-y-1 text-sm text-slate-700">
      <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">{label}</span>
      <input
        className="w-full rounded-md border border-slate-300 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none"
        onChange={(event) => onChange(Number(event.target.value) || 0)}
        type="number"
        value={Math.round(value)}
      />
    </label>
  );
}

function ColorField({
  label,
  value,
  onChange,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <label className="space-y-1 text-sm text-slate-700">
      <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">{label}</span>
      <div className="flex items-center gap-2 rounded-md border border-slate-300 bg-slate-50 px-2 py-1">
        <input className="h-8 w-10 rounded border border-slate-200 bg-white" onChange={(event) => onChange(event.target.value)} type="color" value={value} />
        <span className="text-xs text-slate-500">{value}</span>
      </div>
    </label>
  );
}
