"use client";

import { useEffect, useMemo, useState } from "react";

import { ShapeElement } from "@/components/slides/shape-element";
import { TextBoxElement } from "@/components/slides/text-box-element";
import type { ElementPreviewStyle } from "@/lib/presentation-preview";
import type { ResizeHandle } from "@/components/slides/selection-overlay";
import type { PresentationSlideElement } from "@/types/presentation";

const CANVAS_WIDTH = 1280;
const CANVAS_HEIGHT = 720;
const MIN_WIDTH = 120;
const MIN_HEIGHT = 56;

type ElementFrame = Pick<PresentationSlideElement, "x" | "y" | "width" | "height">;
type InteractionState =
  | {
      mode: "drag";
      startClientX: number;
      startClientY: number;
      startFrame: ElementFrame;
      canvasRect: DOMRect;
    }
  | {
      mode: "resize";
      handle: ResizeHandle;
      startClientX: number;
      startClientY: number;
      startFrame: ElementFrame;
      canvasRect: DOMRect;
    }
  | null;

export function SlideCanvasElement({
  element,
  previewStyle,
  selected,
  onSelect,
  onChange,
  onUpdateFrame,
}: {
  element: PresentationSlideElement;
  previewStyle?: ElementPreviewStyle;
  selected: boolean;
  onSelect: () => void;
  onChange: (text: string) => void;
  onUpdateFrame: (elementId: string, nextFrame: ElementFrame) => void;
}) {
  const [interaction, setInteraction] = useState<InteractionState>(null);

  useEffect(() => {
    if (interaction === null) {
      return;
    }
    const activeInteraction = interaction;

    function handlePointerMove(event: PointerEvent) {
      const dx = (event.clientX - activeInteraction.startClientX) * (CANVAS_WIDTH / activeInteraction.canvasRect.width);
      const dy = (event.clientY - activeInteraction.startClientY) * (CANVAS_HEIGHT / activeInteraction.canvasRect.height);

      if (activeInteraction.mode === "drag") {
        const startFrame = activeInteraction.startFrame;
        onUpdateFrame(element.id, clampFrame({
          ...startFrame,
          x: startFrame.x + dx,
          y: startFrame.y + dy,
        }));
        return;
      }

      const startFrame = activeInteraction.startFrame;
      const resizeHandle = activeInteraction.handle;
      onUpdateFrame(element.id, resizeFrame(startFrame, resizeHandle, dx, dy));
    }

    function handlePointerUp() {
      setInteraction(null);
    }

    window.addEventListener("pointermove", handlePointerMove);
    window.addEventListener("pointerup", handlePointerUp);
    return () => {
      window.removeEventListener("pointermove", handlePointerMove);
      window.removeEventListener("pointerup", handlePointerUp);
    };
  }, [element.id, interaction, onUpdateFrame]);

  const style = useMemo(
    () => ({
      height: `${(element.height / CANVAS_HEIGHT) * 100}%`,
      left: `${(element.x / CANVAS_WIDTH) * 100}%`,
      top: `${(element.y / CANVAS_HEIGHT) * 100}%`,
      width: `${(element.width / CANVAS_WIDTH) * 100}%`,
      zIndex: (element.zIndex ?? element.elementOrder) + 1,
    }),
    [element.elementOrder, element.height, element.width, element.x, element.y, element.zIndex]
  );

  function startInteraction(mode: "drag" | "resize", pointerEvent: React.PointerEvent<HTMLElement>, handle?: ResizeHandle) {
    const canvas = pointerEvent.currentTarget.closest("[data-slide-canvas='true']");
    if (!(canvas instanceof HTMLElement)) {
      return;
    }
    pointerEvent.preventDefault();
    pointerEvent.stopPropagation();
    onSelect();
    if (mode === "resize" && handle) {
      setInteraction({
        mode,
        handle,
        startClientX: pointerEvent.clientX,
        startClientY: pointerEvent.clientY,
        startFrame: {
          x: element.x,
          y: element.y,
          width: element.width,
          height: element.height,
        },
        canvasRect: canvas.getBoundingClientRect(),
      });
      return;
    }
    setInteraction({
      mode: "drag",
      startClientX: pointerEvent.clientX,
      startClientY: pointerEvent.clientY,
      startFrame: {
        x: element.x,
        y: element.y,
        width: element.width,
        height: element.height,
      },
      canvasRect: canvas.getBoundingClientRect(),
    });
  }

  return (
    <div className="absolute" style={style}>
      {element.elementType === "SHAPE" ? (
        <ShapeElement
          element={element}
          onDragStart={(event) => startInteraction("drag", event)}
          onResizeStart={(handle, event) => startInteraction("resize", event, handle)}
          onSelect={onSelect}
          selected={selected}
        />
      ) : (
        <TextBoxElement
          element={element}
          previewStyle={previewStyle}
          onChange={onChange}
          onDragStart={(event) => startInteraction("drag", event)}
          onResizeStart={(handle, event) => startInteraction("resize", event, handle)}
          onSelect={onSelect}
          selected={selected}
        />
      )}
    </div>
  );
}

function clampFrame(frame: ElementFrame): ElementFrame {
  const width = clamp(frame.width, MIN_WIDTH, CANVAS_WIDTH);
  const height = clamp(frame.height, MIN_HEIGHT, CANVAS_HEIGHT);
  return {
    width,
    height,
    x: clamp(frame.x, 0, CANVAS_WIDTH - width),
    y: clamp(frame.y, 0, CANVAS_HEIGHT - height),
  };
}

function resizeFrame(frame: ElementFrame, handle: ResizeHandle, dx: number, dy: number): ElementFrame {
  const next: ElementFrame = { ...frame };

  if (handle.includes("e")) {
    next.width = frame.width + dx;
  }
  if (handle.includes("s")) {
    next.height = frame.height + dy;
  }
  if (handle.includes("w")) {
    next.x = frame.x + dx;
    next.width = frame.width - dx;
  }
  if (handle.includes("n")) {
    next.y = frame.y + dy;
    next.height = frame.height - dy;
  }

  const width = clamp(next.width, MIN_WIDTH, CANVAS_WIDTH);
  const height = clamp(next.height, MIN_HEIGHT, CANVAS_HEIGHT);

  if (handle.includes("w")) {
    next.x += next.width - width;
  }
  if (handle.includes("n")) {
    next.y += next.height - height;
  }

  next.width = width;
  next.height = height;
  return clampFrame(next);
}

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}
