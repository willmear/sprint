"use client";

import { useEffect, useMemo, useState } from "react";

import { TextBoxElement } from "@/components/slides/text-box-element";
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
  selected,
  onSelect,
  onChange,
  onUpdateFrame,
}: {
  element: PresentationSlideElement;
  selected: boolean;
  onSelect: () => void;
  onChange: (text: string) => void;
  onUpdateFrame: (elementId: string, nextFrame: ElementFrame) => void;
}) {
  const [interaction, setInteraction] = useState<InteractionState>(null);

  useEffect(() => {
    if (!interaction) {
      return;
    }

    function handlePointerMove(event: PointerEvent) {
      const dx = (event.clientX - interaction.startClientX) * (CANVAS_WIDTH / interaction.canvasRect.width);
      const dy = (event.clientY - interaction.startClientY) * (CANVAS_HEIGHT / interaction.canvasRect.height);

      if (interaction.mode === "drag") {
        onUpdateFrame(element.id, clampFrame({
          ...interaction.startFrame,
          x: interaction.startFrame.x + dx,
          y: interaction.startFrame.y + dy,
        }));
        return;
      }

      onUpdateFrame(element.id, resizeFrame(interaction.startFrame, interaction.handle, dx, dy));
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
      zIndex: element.elementOrder + 1,
    }),
    [element.elementOrder, element.height, element.width, element.x, element.y]
  );

  function startInteraction(mode: InteractionState["mode"], pointerEvent: React.PointerEvent<HTMLElement>, handle?: ResizeHandle) {
    const canvas = pointerEvent.currentTarget.closest("[data-slide-canvas='true']");
    if (!(canvas instanceof HTMLElement)) {
      return;
    }
    pointerEvent.preventDefault();
    pointerEvent.stopPropagation();
    onSelect();
    setInteraction({
      mode,
      handle: handle as ResizeHandle,
      startClientX: pointerEvent.clientX,
      startClientY: pointerEvent.clientY,
      startFrame: {
        x: element.x,
        y: element.y,
        width: element.width,
        height: element.height,
      },
      canvasRect: canvas.getBoundingClientRect(),
    } as InteractionState);
  }

  return (
    <div className="absolute" style={style}>
      <TextBoxElement
        element={element}
        onChange={onChange}
        onDragStart={(event) => startInteraction("drag", event)}
        onResizeStart={(handle, event) => startInteraction("resize", event, handle)}
        onSelect={onSelect}
        selected={selected}
      />
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
