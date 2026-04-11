import { Textarea } from "@/components/ui/textarea";
import type { PresentationSlide, PresentationSlideElement } from "@/types/presentation";

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
  onUpdateNotes,
}: {
  slide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  onUpdateNotes: (notes: string) => void;
}) {
  return (
    <aside className="flex h-full min-h-0 flex-col border-l border-slate-200 bg-white">
      <div className="border-b border-slate-200 px-4 py-3">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-500">Inspector</p>
        <p className="mt-1 text-sm text-slate-600">Slide details and presenter notes</p>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        <section className="space-y-3">
          <h3 className="text-sm font-semibold text-slate-900">Current slide</h3>
          <InspectorField label="Title" value={slide?.title || "No slide selected"} />
          <InspectorField label="Type" value={slide?.slideType?.replaceAll("_", " ") || "-"} />
          <InspectorField label="Layout" value={slide?.layoutType?.replaceAll("_", " ") || "-"} />
        </section>

        <section className="space-y-3 border-t border-slate-200 pt-4">
          <h3 className="text-sm font-semibold text-slate-900">Selected object</h3>
          {!selectedElement ? (
            <p className="text-sm text-slate-500">Select a text box on the canvas to inspect its position and formatting.</p>
          ) : (
            <div className="space-y-3">
              <InspectorField label="Font" value={`${selectedElement.fontFamily} ${selectedElement.fontSize}px`} />
              <InspectorField label="Frame" value={`${Math.round(selectedElement.x)}, ${Math.round(selectedElement.y)} • ${Math.round(selectedElement.width)} × ${Math.round(selectedElement.height)}`} />
              <InspectorField label="Alignment" value={selectedElement.textAlignment} />
              <div className="space-y-1">
                <div className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Text</div>
                <div className="rounded-md border border-slate-200 bg-slate-50 px-3 py-2 text-sm leading-relaxed text-slate-700 whitespace-pre-wrap">
                  {selectedElement.textContent || "Empty text box"}
                </div>
              </div>
            </div>
          )}
        </section>

        <section className="space-y-3 border-t border-slate-200 pt-4">
          <h3 className="text-sm font-semibold text-slate-900">Speaker notes</h3>
          <Textarea
            className="min-h-48 rounded-md border-slate-300 bg-slate-50 text-sm shadow-none"
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
