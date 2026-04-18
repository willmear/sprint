"use client";

import { useEffect, useRef, useState } from "react";

import { SelectionOverlay, type ResizeHandle } from "@/components/slides/selection-overlay";
import { cn } from "@/lib/utils/cn";
import type { PresentationSlideElement } from "@/types/presentation";

export function TextBoxElement({
  element,
  selected,
  onSelect,
  onChange,
  onDragStart,
  onResizeStart,
}: {
  element: PresentationSlideElement;
  selected: boolean;
  onSelect: () => void;
  onChange: (text: string) => void;
  onDragStart: (event: React.PointerEvent<HTMLElement>) => void;
  onResizeStart: (handle: ResizeHandle, event: React.PointerEvent<HTMLButtonElement>) => void;
}) {
  const editorRef = useRef<HTMLDivElement | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    if (!editorRef.current || isEditing) {
      return;
    }
    if (editorRef.current.innerText !== element.textContent) {
      editorRef.current.innerText = element.textContent;
    }
  }, [element.textContent, isEditing]);

  useEffect(() => {
    if (!selected && isEditing) {
      setIsEditing(false);
    }
  }, [selected, isEditing]);

  useEffect(() => {
    if (!selected || !isEditing || !editorRef.current) {
      return;
    }
    if (document.activeElement === editorRef.current) {
      return;
    }
    editorRef.current.focus();
    const selection = window.getSelection();
    if (!selection) {
      return;
    }
    const range = document.createRange();
    range.selectNodeContents(editorRef.current);
    range.collapse(false);
    selection.removeAllRanges();
    selection.addRange(range);
  }, [selected, isEditing, element.id]);

  return (
    <div
      className={cn("group relative h-full w-full", selected ? "cursor-move" : "cursor-pointer")}
      onMouseDown={(event) => event.stopPropagation()}
      onPointerDown={(event) => {
        if (event.button !== 0 || isEditing) {
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
        ref={editorRef}
        className={cn(
          "h-full w-full overflow-hidden rounded-[2px] border border-transparent bg-transparent px-2 py-1 outline-none transition",
          selected ? "bg-blue-50/35" : "hover:border-slate-200 hover:bg-white/70",
          selected && isEditing ? "cursor-text" : "cursor-inherit"
        )}
        contentEditable={selected && isEditing}
        onBlur={(event) => {
          setIsEditing(false);
          onChange(event.currentTarget.innerText);
        }}
        onClick={(event) => {
          event.stopPropagation();
          onSelect();
        }}
        onDoubleClick={(event) => {
          event.stopPropagation();
          onSelect();
          setIsEditing(true);
        }}
        onFocus={() => setIsEditing(true)}
        onInput={(event) => onChange(event.currentTarget.innerText)}
        role="textbox"
        spellCheck={false}
        suppressContentEditableWarning
        style={{
          color: element.textColor || "#0f172a",
          fontFamily: element.fontFamily,
          fontSize: `${element.fontSize}px`,
          fontStyle: element.italic ? "italic" : "normal",
          fontWeight: element.bold ? 700 : 400,
          letterSpacing: element.role === "TITLE" ? "-0.02em" : "0",
          lineHeight: element.role === "TITLE" ? 1.1 : 1.3,
          textAlign: element.textAlignment.toLowerCase() as "left" | "center" | "right",
          textDecoration: element.underline ? "underline" : "none",
          whiteSpace: "pre-wrap",
        }}
      />
      {selected ? <SelectionOverlay label="Text box" onResizeStart={onResizeStart} /> : null}
    </div>
  );
}
