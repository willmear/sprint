"use client";

import { useEffect, useMemo, useRef, useState } from "react";

import { Card } from "@/components/ui/card";
import type { PresentationSlide, PresentationSlideElement } from "@/types/presentation";

const CANVAS_WIDTH = 1280;
const CANVAS_HEIGHT = 720;

type InteractionState =
  | {
      type: "drag";
      elementId: string;
      startClientX: number;
      startClientY: number;
      originX: number;
      originY: number;
    }
  | {
      type: "resize";
      elementId: string;
      startClientX: number;
      startClientY: number;
      originWidth: number;
      originHeight: number;
    }
  | null;

export function SlideCanvasPreview({
  slide,
  deckTitle,
  deckSubtitle,
  previewMode,
  selectedElementId,
  onSelectElement,
  onUpdateElement,
}: {
  slide: PresentationSlide | null;
  deckTitle: string;
  deckSubtitle?: string | null;
  previewMode: boolean;
  selectedElementId: string | null;
  onSelectElement: (elementId: string | null) => void;
  onUpdateElement: (elementId: string, updater: (element: PresentationSlideElement) => PresentationSlideElement) => void;
}) {
  const shellRef = useRef<HTMLDivElement | null>(null);
  const [scale, setScale] = useState(1);
  const [interaction, setInteraction] = useState<InteractionState>(null);

  const selectedElement = useMemo(
    () => slide?.elements.find((element) => element.id === selectedElementId) ?? null,
    [selectedElementId, slide?.elements],
  );

  useEffect(() => {
    const shell = shellRef.current;
    if (!shell) {
      return;
    }
    const observer = new ResizeObserver(([entry]) => {
      const nextScale = Math.min(entry.contentRect.width / CANVAS_WIDTH, 1);
      setScale(nextScale || 1);
    });
    observer.observe(shell);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    if (interaction === null || !slide || previewMode) {
      return;
    }
    const activeInteraction = interaction;

    function handlePointerMove(event: PointerEvent) {
      if (!shellRef.current) {
        return;
      }
      const deltaX = (event.clientX - activeInteraction.startClientX) / scale;
      const deltaY = (event.clientY - activeInteraction.startClientY) / scale;
      if (activeInteraction.type === "drag") {
        const originX = activeInteraction.originX;
        const originY = activeInteraction.originY;
        onUpdateElement(activeInteraction.elementId, (element) => ({
          ...element,
          x: clamp(element.width, originX + deltaX, CANVAS_WIDTH),
          y: clamp(element.height, originY + deltaY, CANVAS_HEIGHT),
        }));
        return;
      }
      const originWidth = activeInteraction.originWidth;
      const originHeight = activeInteraction.originHeight;
      onUpdateElement(activeInteraction.elementId, (element) => ({
        ...element,
        width: Math.max(120, Math.min(CANVAS_WIDTH - element.x, originWidth + deltaX)),
        height: Math.max(56, Math.min(CANVAS_HEIGHT - element.y, originHeight + deltaY)),
      }));
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
  }, [interaction, onUpdateElement, previewMode, scale, slide]);

  if (!slide) {
    return (
      <Card className="flex min-h-[32rem] items-center justify-center bg-white">
        <p className="text-sm text-stone-600">Select a slide to preview it.</p>
      </Card>
    );
  }

  return (
    <Card className="overflow-hidden bg-gradient-to-br from-stone-50 via-white to-stone-100 p-4 shadow-panel">
      <div className="flex items-center justify-between gap-4 border-b border-line pb-4">
        <div>
          <p className="text-xs uppercase tracking-[0.18em] text-stone-500">Slide canvas</p>
          <p className="mt-1 text-sm text-stone-600">
            {previewMode ? "Previewing the slide" : "Click a text box to edit, drag the handle to move it, and use the corner to resize it."}
          </p>
        </div>
        <div className="text-right text-xs text-stone-500">
          <p>{slide.slideType.replaceAll("_", " ")}</p>
          <p className="mt-1">{deckSubtitle || deckTitle}</p>
        </div>
      </div>

      <div className="mt-6" ref={shellRef}>
        <div className="mx-auto" style={{ height: CANVAS_HEIGHT * scale, width: CANVAS_WIDTH * scale }}>
          <div
            className="relative rounded-[2rem] border border-line bg-white shadow-[0_28px_80px_rgba(15,23,42,0.12)]"
            onClick={() => {
              if (!previewMode) {
                onSelectElement(null);
              }
            }}
            style={{
              height: CANVAS_HEIGHT,
              transform: `scale(${scale})`,
              transformOrigin: "top left",
              width: CANVAS_WIDTH,
            }}
          >
            {slide.elements.map((element) => {
              const selected = element.id === selectedElement?.id;
              return (
                <div
                  key={element.id}
                  className={selected && !previewMode ? "absolute rounded-md ring-2 ring-blue-500 ring-offset-2" : "absolute"}
                  onClick={(event) => {
                    event.stopPropagation();
                    if (!previewMode) {
                      onSelectElement(element.id);
                    }
                  }}
                  style={{
                    height: element.height,
                    left: element.x,
                    top: element.y,
                    width: element.width,
                  }}
                >
                  {selected && !previewMode ? (
                    <button
                      className="absolute -top-8 left-0 rounded-full bg-blue-600 px-3 py-1 text-xs font-semibold text-white shadow"
                      onPointerDown={(event) => {
                        event.preventDefault();
                        event.stopPropagation();
                        setInteraction({
                          type: "drag",
                          elementId: element.id,
                          startClientX: event.clientX,
                          startClientY: event.clientY,
                          originX: element.x,
                          originY: element.y,
                        });
                      }}
                      type="button"
                    >
                      Move
                    </button>
                  ) : null}

                  <div
                    className="h-full w-full overflow-hidden rounded-md px-2 py-1 outline-none"
                    contentEditable={!previewMode}
                    onBlur={(event) => {
                      if (previewMode) {
                        return;
                      }
                      onUpdateElement(element.id, (current) => ({ ...current, textContent: event.currentTarget.innerText }));
                    }}
                    onInput={(event) => {
                      if (previewMode) {
                        return;
                      }
                      const textValue = event.currentTarget.innerText;
                      onUpdateElement(element.id, (current) => ({ ...current, textContent: textValue }));
                    }}
                    onMouseDown={(event) => event.stopPropagation()}
                    onFocus={() => {
                      if (!previewMode) {
                        onSelectElement(element.id);
                      }
                    }}
                    suppressContentEditableWarning
                    style={{
                      fontFamily: element.fontFamily,
                      fontSize: element.fontSize,
                      fontStyle: element.italic ? "italic" : "normal",
                      fontWeight: element.bold ? 700 : 400,
                      lineHeight: 1.25,
                      textAlign: element.textAlignment.toLowerCase() as "left" | "center" | "right",
                      whiteSpace: "pre-wrap",
                    }}
                  >
                    {element.textContent}
                  </div>

                  {selected && !previewMode ? (
                    <button
                      className="absolute -bottom-3 -right-3 h-6 w-6 rounded-full border-2 border-white bg-blue-600 shadow"
                      onPointerDown={(event) => {
                        event.preventDefault();
                        event.stopPropagation();
                        setInteraction({
                          type: "resize",
                          elementId: element.id,
                          startClientX: event.clientX,
                          startClientY: event.clientY,
                          originWidth: element.width,
                          originHeight: element.height,
                        });
                      }}
                      type="button"
                    />
                  ) : null}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </Card>
  );
}

function clamp(size: number, value: number, max: number) {
  return Math.max(0, Math.min(max - size, value));
}
