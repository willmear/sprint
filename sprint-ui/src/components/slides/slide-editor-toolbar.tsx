import type { ReactNode } from "react";

import type { PresentationSlideElement, ShapeType, SlideElementRole } from "@/types/presentation";

const fonts = ["Aptos", "Arial", "Georgia", "Times New Roman", "Menlo", "Helvetica", "Verdana", "Calibri", "Trebuchet MS"];
const shapeTypes: Array<{ label: string; value: ShapeType }> = [
  { label: "Rectangle", value: "RECTANGLE" },
  { label: "Rounded", value: "ROUNDED_RECTANGLE" },
  { label: "Circle", value: "CIRCLE" },
  { label: "Ellipse", value: "ELLIPSE" },
  { label: "Diamond", value: "DIAMOND" },
  { label: "Arrow", value: "ARROW" },
  { label: "Line", value: "LINE" },
  { label: "Triangle", value: "TRIANGLE" },
];

function ToolbarGroup({ children }: { children: ReactNode }) {
  return <div className="flex items-center gap-2 rounded-md border border-slate-200 bg-white px-2 py-1.5">{children}</div>;
}

function ToolbarButton({
  active,
  children,
  disabled,
  onClick,
}: {
  active?: boolean;
  children: ReactNode;
  disabled?: boolean;
  onClick: () => void;
}) {
  return (
    <button
      className={active ? "rounded-md bg-slate-900 px-3 py-2 text-xs font-semibold text-white" : "rounded-md bg-slate-100 px-3 py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"}
      disabled={disabled}
      onClick={onClick}
      type="button"
    >
      {children}
    </button>
  );
}

export function SlideEditorToolbar({
  selectedElement,
  onAddTextBox,
  onAddShape,
  onDuplicateElement,
  onDeleteElement,
  onLayerBackward,
  onLayerForward,
  onSetElementRole,
  onChangeFormatting,
}: {
  selectedElement: PresentationSlideElement | null;
  onAddTextBox: () => void;
  onAddShape: (shapeType: ShapeType) => void;
  onDuplicateElement: () => void;
  onDeleteElement: () => void;
  onLayerBackward: () => void;
  onLayerForward: () => void;
  onSetElementRole: (role: SlideElementRole) => void;
  onChangeFormatting: (
    updates: Partial<
      Pick<
        PresentationSlideElement,
        | "fontFamily"
        | "fontSize"
        | "bold"
        | "italic"
        | "underline"
        | "textAlignment"
        | "textColor"
        | "fillColor"
        | "borderColor"
        | "borderWidth"
      >
    >
  ) => void;
}) {
  const disabled = !selectedElement;
  const isTextElement = selectedElement?.elementType !== "SHAPE";
  const bulletActive = selectedElement?.role === "BODY_BULLETS";

  return (
    <div className="border-b border-slate-200 bg-[#f7f8fa] px-4 py-2">
      <div className="flex flex-wrap items-center gap-2">
        <ToolbarGroup>
          <ToolbarButton onClick={onAddTextBox}>Add text</ToolbarButton>
          <select
            className="rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-700 outline-none"
            defaultValue=""
            onChange={(event) => {
              if (!event.target.value) {
                return;
              }
              onAddShape(event.target.value as ShapeType);
              event.target.value = "";
            }}
          >
            <option value="">Add shape</option>
            {shapeTypes.map((shape) => (
              <option key={shape.value} value={shape.value}>
                {shape.label}
              </option>
            ))}
          </select>
          <ToolbarButton disabled={disabled} onClick={onDuplicateElement}>Duplicate</ToolbarButton>
          <ToolbarButton disabled={disabled} onClick={onDeleteElement}>Delete</ToolbarButton>
        </ToolbarGroup>

        <ToolbarGroup>
          <ToolbarButton disabled={disabled} onClick={onLayerBackward}>Send back</ToolbarButton>
          <ToolbarButton disabled={disabled} onClick={onLayerForward}>Bring forward</ToolbarButton>
        </ToolbarGroup>

        <ToolbarGroup>
          <select
            className="rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-700 outline-none disabled:opacity-50"
            disabled={disabled}
            onChange={(event) => onChangeFormatting({ fontFamily: event.target.value })}
            value={selectedElement?.fontFamily || "Aptos"}
          >
            {fonts.map((font) => (
              <option key={font} value={font}>
                {font}
              </option>
            ))}
          </select>
          <input
            className="w-20 rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-700 outline-none disabled:opacity-50"
            disabled={!isTextElement || disabled}
            max={144}
            min={8}
            onChange={(event) => onChangeFormatting({ fontSize: Math.min(144, Math.max(8, Number(event.target.value) || 18)) })}
            type="number"
            value={selectedElement?.fontSize || 24}
          />
          <ToolbarButton active={selectedElement?.bold} disabled={!isTextElement || disabled} onClick={() => onChangeFormatting({ bold: !selectedElement?.bold })}>
            Bold
          </ToolbarButton>
          <ToolbarButton active={selectedElement?.italic} disabled={!isTextElement || disabled} onClick={() => onChangeFormatting({ italic: !selectedElement?.italic })}>
            Italic
          </ToolbarButton>
          <ToolbarButton active={selectedElement?.underline} disabled={!isTextElement || disabled} onClick={() => onChangeFormatting({ underline: !selectedElement?.underline })}>
            Underline
          </ToolbarButton>
          <ToolbarButton active={bulletActive} disabled={!isTextElement || disabled} onClick={() => onSetElementRole(bulletActive ? "BODY" : "BODY_BULLETS")}>
            Bullets
          </ToolbarButton>
          <select
            className="rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-700 outline-none disabled:opacity-50"
            disabled={!isTextElement || disabled}
            onChange={(event) => onChangeFormatting({ textAlignment: event.target.value as PresentationSlideElement["textAlignment"] })}
            value={selectedElement?.textAlignment || "LEFT"}
          >
            <option value="LEFT">Left</option>
            <option value="CENTER">Center</option>
            <option value="RIGHT">Right</option>
          </select>
        </ToolbarGroup>

        <ToolbarGroup>
          <label className="flex items-center gap-2 text-xs font-medium text-slate-600">
            Text
            <input
              className="h-8 w-10 rounded border border-slate-200 bg-white"
              disabled={!isTextElement || disabled}
              onChange={(event) => onChangeFormatting({ textColor: event.target.value })}
              type="color"
              value={selectedElement?.textColor || "#0f172a"}
            />
          </label>
          <label className="flex items-center gap-2 text-xs font-medium text-slate-600">
            Fill
            <input
              className="h-8 w-10 rounded border border-slate-200 bg-white"
              disabled={disabled}
              onChange={(event) => onChangeFormatting({ fillColor: event.target.value })}
              type="color"
              value={selectedElement?.fillColor || (selectedElement?.elementType === "SHAPE" ? "#dbeafe" : "#ffffff")}
            />
          </label>
          <label className="flex items-center gap-2 text-xs font-medium text-slate-600">
            Border
            <input
              className="h-8 w-10 rounded border border-slate-200 bg-white"
              disabled={disabled}
              onChange={(event) => onChangeFormatting({ borderColor: event.target.value })}
              type="color"
              value={selectedElement?.borderColor || "#2563eb"}
            />
          </label>
          <input
            className="w-16 rounded-md border border-slate-200 bg-white px-2 py-2 text-xs font-medium text-slate-700 outline-none disabled:opacity-50"
            disabled={disabled}
            min={0}
            onChange={(event) => onChangeFormatting({ borderWidth: Math.max(0, Number(event.target.value) || 0) })}
            type="number"
            value={selectedElement?.borderWidth ?? 2}
          />
        </ToolbarGroup>

        <div className="ml-auto rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-500">
          {selectedElement ? `${selectedElement.elementType === "SHAPE" ? "Shape" : "Text"} selected` : "Select an object to format"}
        </div>
      </div>
    </div>
  );
}
