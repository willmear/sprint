import { PRESENTATION_THEMES } from "@/lib/presentation-themes";
import { Textarea } from "@/components/ui/textarea";
import type { PresentationSlide, PresentationSlideElement, PresentationThemeSummary, SlideElementRole } from "@/types/presentation";

const elementRoles: Array<{ label: string; value: SlideElementRole }> = [
  { label: "Title", value: "TITLE" },
  { label: "Subtitle", value: "SUBTITLE" },
  { label: "Body", value: "BODY" },
  { label: "Bullets", value: "BODY_BULLETS" },
  { label: "Section label", value: "SECTION_LABEL" },
  { label: "Callout", value: "CALLOUT" },
  { label: "Metric", value: "METRIC" },
  { label: "Footer", value: "FOOTER" },
  { label: "Freeform", value: "FREEFORM" },
];

function InspectorField({ label, value }: { label: string; value: string }) {
  return (
    <div className="space-y-1">
      <div className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">{label}</div>
      <div className="rounded-md border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">{value}</div>
    </div>
  );
}

function InspectorSection({
  title,
  subtitle,
  defaultOpen = false,
  children,
}: {
  title: string;
  subtitle?: string;
  defaultOpen?: boolean;
  children: React.ReactNode;
}) {
  return (
    <details className="group rounded-xl border border-slate-200 bg-white" open={defaultOpen}>
      <summary className="flex cursor-pointer list-none items-center justify-between gap-3 px-4 py-3">
        <div>
          <p className="text-sm font-semibold text-slate-900">{title}</p>
          {subtitle ? <p className="mt-1 text-xs text-slate-500">{subtitle}</p> : null}
        </div>
        <span className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-400 transition group-open:rotate-180">⌄</span>
      </summary>
      <div className="border-t border-slate-200 px-4 py-4">{children}</div>
    </details>
  );
}

export function SlideInspectorPanel({
  slide,
  selectedElement,
  deckTheme,
  onThemeChange,
  onUpdateNotes,
  onSlideStyleChange,
  onSelectedElementChange,
}: {
  slide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  deckTheme?: PresentationThemeSummary | null;
  onThemeChange: (themeId: string) => void;
  onUpdateNotes: (notes: string) => void;
  onSlideStyleChange: (updates: Partial<Pick<PresentationSlide, "backgroundColor" | "showGrid">>) => void;
  onSelectedElementChange: (
    updates: Partial<
      Pick<
        PresentationSlideElement,
        "role" | "textContent" | "fontFamily" | "fontSize" | "bold" | "italic" | "underline" | "textAlignment" | "x" | "y" | "width" | "height" | "fillColor" | "borderColor" | "borderWidth" | "textColor"
      >
    >
  ) => void;
}) {
  const activeTheme = deckTheme || PRESENTATION_THEMES[0];
  const suggestedBackground = slide?.backgroundColor || activeTheme.colorPalette.background;
  const isTextElement = selectedElement?.elementType !== "SHAPE";

  return (
    <aside className="flex h-full min-h-0 flex-col border-l border-slate-200 bg-[#fbfcfd]">
      <div className="border-b border-slate-200 px-4 py-3">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-500">Inspector</p>
        <p className="mt-1 text-sm text-slate-600">Themes, formatting, slide settings, speaker notes</p>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        <InspectorSection defaultOpen title="Theme" subtitle="Open palette menu on right, choose deck look">
          <div className="grid gap-3">
            {PRESENTATION_THEMES.map((theme) => {
              const active = theme.themeId === activeTheme.themeId;
              return (
                <button
                  key={theme.themeId}
                  className={active ? "rounded-xl border border-slate-900 bg-slate-50 p-3 text-left shadow-sm" : "rounded-xl border border-slate-200 bg-white p-3 text-left transition hover:border-slate-300 hover:bg-slate-50"}
                  onClick={() => onThemeChange(theme.themeId)}
                  type="button"
                >
                  <div className="flex items-center justify-between gap-3">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{theme.displayName}</p>
                      <p className="mt-1 text-xs text-slate-500">{theme.typography.titleFontFamily} / {theme.colorPalette.accent}</p>
                    </div>
                    {active ? <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-700">Active</span> : null}
                  </div>
                  <div className="mt-3 flex gap-2">
                    {[theme.colorPalette.background, theme.colorPalette.surface, theme.colorPalette.accent, theme.colorPalette.textPrimary].map((color, index) => (
                      <span key={`${theme.themeId}-${index}-${color}`} className="h-6 flex-1 rounded-md border border-black/5" style={{ backgroundColor: color }} />
                    ))}
                  </div>
                </button>
              );
            })}
          </div>
        </InspectorSection>

        <InspectorSection defaultOpen title="Current slide" subtitle="Deck structure and slide appearance">
          <div className="space-y-3">
            <InspectorField label="Title" value={slide?.title || "No slide selected"} />
            <InspectorField label="Type" value={slide?.slideType?.replaceAll("_", " ") || "-"} />
            <InspectorField label="Layout" value={slide?.layoutType?.replaceAll("_", " ") || "-"} />
            <label className="space-y-1 text-sm text-slate-700">
              <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Background</span>
              <div className="flex items-center gap-3">
                <input
                  className="h-10 w-14 rounded border border-slate-200 bg-white"
                  disabled={!slide}
                  onChange={(event) => onSlideStyleChange({ backgroundColor: event.target.value })}
                  type="color"
                  value={suggestedBackground}
                />
                <span className="text-xs text-slate-500">{suggestedBackground}</span>
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
          </div>
        </InspectorSection>

        <InspectorSection defaultOpen title="Selected object" subtitle="Text, bullets, role, frame, colors">
          {!selectedElement ? (
            <p className="text-sm text-slate-500">Select text box or shape on canvas.</p>
          ) : (
            <div className="space-y-4">
              <InspectorField label="Kind" value={selectedElement.elementType === "SHAPE" ? `Shape${selectedElement.shapeType ? ` • ${selectedElement.shapeType}` : ""}` : "Text box"} />

              <label className="space-y-1 text-sm text-slate-700">
                <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Role</span>
                <select
                  className="w-full rounded-md border border-slate-300 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none"
                  disabled={!selectedElement}
                  onChange={(event) => onSelectedElementChange({ role: event.target.value as SlideElementRole })}
                  value={selectedElement.role}
                >
                  {elementRoles.map((role) => (
                    <option key={role.value} value={role.value}>
                      {role.label}
                    </option>
                  ))}
                </select>
              </label>

              {isTextElement ? (
                <>
                  <div className="grid grid-cols-2 gap-3">
                    <NumberField label="Font size" onChange={(value) => onSelectedElementChange({ fontSize: value })} value={selectedElement.fontSize} />
                    <label className="space-y-1 text-sm text-slate-700">
                      <span className="text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-500">Align</span>
                      <select
                        className="w-full rounded-md border border-slate-300 bg-slate-50 px-3 py-2 text-sm text-slate-700 outline-none"
                        onChange={(event) => onSelectedElementChange({ textAlignment: event.target.value as PresentationSlideElement["textAlignment"] })}
                        value={selectedElement.textAlignment}
                      >
                        <option value="LEFT">Left</option>
                        <option value="CENTER">Center</option>
                        <option value="RIGHT">Right</option>
                      </select>
                    </label>
                  </div>

                  <div className="flex flex-wrap gap-2">
                    <MiniToggle active={selectedElement.bold} label="Bold" onClick={() => onSelectedElementChange({ bold: !selectedElement.bold })} />
                    <MiniToggle active={selectedElement.italic} label="Italic" onClick={() => onSelectedElementChange({ italic: !selectedElement.italic })} />
                    <MiniToggle active={selectedElement.underline ?? false} label="Underline" onClick={() => onSelectedElementChange({ underline: !(selectedElement.underline ?? false) })} />
                    <MiniToggle active={selectedElement.role === "BODY_BULLETS"} label="Bullets" onClick={() => onSelectedElementChange({ role: selectedElement.role === "BODY_BULLETS" ? "BODY" : "BODY_BULLETS" })} />
                  </div>
                </>
              ) : null}

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
        </InspectorSection>

        <InspectorSection title="Speaker notes" subtitle="Presenter notes and talking points">
          <Textarea
            className="min-h-48 rounded-md border-slate-300 bg-slate-50 px-3 py-2 text-sm shadow-none"
            disabled={!slide}
            onChange={(event) => onUpdateNotes(event.target.value)}
            placeholder="Add talking points, transitions, or presenter reminders."
            value={slide?.speakerNotes || ""}
          />
        </InspectorSection>
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

function MiniToggle({
  active,
  label,
  onClick,
}: {
  active: boolean;
  label: string;
  onClick: () => void;
}) {
  return (
    <button
      className={active ? "rounded-md bg-slate-900 px-3 py-2 text-xs font-semibold text-white" : "rounded-md border border-slate-300 bg-slate-50 px-3 py-2 text-xs font-semibold text-slate-700"}
      onClick={onClick}
      type="button"
    >
      {label}
    </button>
  );
}
