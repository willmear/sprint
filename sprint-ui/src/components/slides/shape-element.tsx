"use client";

import { SelectionOverlay, type ResizeHandle } from "@/components/slides/selection-overlay";
import { cn } from "@/lib/utils/cn";
import type { PresentationSlideElement } from "@/types/presentation";

export function ShapeElement({
  element,
  selected,
  onSelect,
  onDragStart,
  onResizeStart,
}: {
  element: PresentationSlideElement;
  selected: boolean;
  onSelect: () => void;
  onDragStart: (event: React.PointerEvent<HTMLElement>) => void;
  onResizeStart: (handle: ResizeHandle, event: React.PointerEvent<HTMLButtonElement>) => void;
}) {
  return (
    <div
      className={cn("group relative flex h-full w-full items-center justify-center", selected ? "cursor-move" : "cursor-pointer")}
      onMouseDown={(event) => event.stopPropagation()}
      onPointerDown={(event) => {
        if (event.button !== 0) {
          return;
        }
        const target = event.target as HTMLElement;
        if (target.closest("button")) {
          return;
        }
        event.stopPropagation();
        onSelect();
        onDragStart(event);
      }}
    >
      <div
        className={cn(
          "pointer-events-none h-full w-full border transition",
          element.shapeType === "ELLIPSE" || element.shapeType === "CIRCLE" ? "rounded-full" : "",
          element.shapeType === "ROUNDED_RECTANGLE" ? "rounded-[24px]" : "",
          element.shapeType === "LINE" ? "rounded-full" : ""
        )}
        style={shapeStyle(element)}
      />
      {element.textContent ? (
        <div
          className="pointer-events-none absolute inset-0 flex items-center justify-center px-4 text-center text-sm font-semibold"
          style={{
            color: element.textColor || "#0f172a",
            fontFamily: element.fontFamily,
            fontSize: `${Math.max(12, element.fontSize)}px`,
            fontStyle: element.italic ? "italic" : "normal",
            fontWeight: element.bold ? 700 : 500,
            textDecoration: element.underline ? "underline" : "none",
          }}
        >
          {element.textContent}
        </div>
      ) : null}
      {selected ? <SelectionOverlay label="Shape" onResizeStart={onResizeStart} /> : null}
    </div>
  );
}

function shapeStyle(element: PresentationSlideElement) {
  const fillColor = element.fillColor || "#dbeafe";
  const borderColor = element.borderColor || "#2563eb";
  const borderWidth = Math.max(1, element.borderWidth || 2);
  const rotation = element.rotationDegrees || 0;

  if (element.shapeType === "TRIANGLE") {
    return {
      backgroundColor: "transparent",
      borderBottom: `${element.height}px solid ${fillColor}`,
      borderLeft: `${element.width / 2}px solid transparent`,
      borderRight: `${element.width / 2}px solid transparent`,
      borderTop: "0 solid transparent",
      height: 0,
      transform: `rotate(${rotation}deg)`,
      width: 0,
    };
  }

  if (element.shapeType === "DIAMOND") {
    return {
      backgroundColor: fillColor,
      border: `${borderWidth}px solid ${borderColor}`,
      transform: `rotate(${45 + rotation}deg)`,
    };
  }

  if (element.shapeType === "ARROW") {
    return {
      background: `linear-gradient(90deg, ${fillColor} 0%, ${fillColor} 72%, transparent 72%)`,
      border: `${borderWidth}px solid ${borderColor}`,
      clipPath: "polygon(0 25%, 72% 25%, 72% 0, 100% 50%, 72% 100%, 72% 75%, 0 75%)",
      transform: `rotate(${rotation}deg)`,
    };
  }

  if (element.shapeType === "LINE") {
    return {
      backgroundColor: borderColor,
      border: "none",
      height: `${Math.max(2, borderWidth)}px`,
      left: 0,
      position: "absolute" as const,
      top: "50%",
      transform: `translateY(-50%) rotate(${rotation}deg)`,
      width: "100%",
    };
  }

  return {
    backgroundColor: fillColor,
    border: `${borderWidth}px solid ${borderColor}`,
    transform: `rotate(${rotation}deg)`,
  };
}
