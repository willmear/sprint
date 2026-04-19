"use client";

import Link from "next/link";
import { use, useEffect, useMemo, useState } from "react";

import { SlideEditorShell } from "@/components/slides/slide-editor-shell";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { downloadBlob } from "@/lib/export-client";
import { resolvePresentationTheme } from "@/lib/presentation-themes";
import { ApiError } from "@/lib/api/client";
import {
  useAddSlide,
  useDeleteSlide,
  useDuplicateSlide,
  usePresentationDeck,
  useReorderSlides,
  useSavePresentationDeck,
} from "@/lib/hooks/use-presentation";
import { exportService } from "@/services/export.service";
import type {
  AddSlideRequest,
  PresentationDeck,
  PresentationSlide,
  PresentationSlideElement,
  ShapeType,
  SlideElementRole,
  UpdateDeckRequest,
} from "@/types/presentation";

const CANVAS_WIDTH = 1280;
const CANVAS_HEIGHT = 720;
const DEFAULT_TEXTBOX_WIDTH = 360;
const DEFAULT_TEXTBOX_HEIGHT = 120;
const DEFAULT_TEXTBOX_X = 220;
const DEFAULT_TEXTBOX_Y = 180;

export default function SlideEditorPage({
  params,
}: {
  params: Promise<{ workspaceId: string; sprintId: string }>;
}) {
  const { workspaceId, sprintId } = use(params);
  const deckQuery = usePresentationDeck(workspaceId, sprintId);
  const saveDeck = useSavePresentationDeck(workspaceId, sprintId);
  const addSlide = useAddSlide(workspaceId, sprintId);
  const duplicateSlide = useDuplicateSlide(workspaceId, sprintId);
  const deleteSlide = useDeleteSlide(workspaceId, sprintId);
  const reorderSlides = useReorderSlides(workspaceId, sprintId);

  const [draftDeck, setDraftDeck] = useState<PresentationDeck | null>(null);
  const [selectedSlideId, setSelectedSlideId] = useState<string | null>(null);
  const [selectedElementId, setSelectedElementId] = useState<string | null>(null);
  const [dirty, setDirty] = useState(false);
  const [editorMessage, setEditorMessage] = useState<string | null>(null);
  const [exportPending, setExportPending] = useState(false);

  useEffect(() => {
    if (!deckQuery.data) {
      return;
    }
    const normalizedDeck = normalizeDeck(deckQuery.data);
    setDraftDeck(normalizedDeck);
    setDirty(false);
    setSelectedSlideId((current) => {
      if (current && normalizedDeck.slides.some((slide) => slide.id === current)) {
        return current;
      }
      return normalizedDeck.slides[0]?.id ?? null;
    });
  }, [deckQuery.data]);

  const selectedSlide = useMemo(() => {
    if (!draftDeck) {
      return null;
    }
    return draftDeck.slides.find((slide) => slide.id === selectedSlideId) ?? draftDeck.slides[0] ?? null;
  }, [draftDeck, selectedSlideId]);

  const selectedElement = useMemo(() => {
    if (!selectedSlide) {
      return null;
    }
    return selectedSlide.elements.find((element) => element.id === selectedElementId) ?? null;
  }, [selectedElementId, selectedSlide]);

  useEffect(() => {
    if (!selectedSlide) {
      setSelectedElementId(null);
      return;
    }
    setSelectedElementId((current) => {
      if (current && selectedSlide.elements.some((element) => element.id === current)) {
        return current;
      }
      return null;
    });
  }, [selectedSlide]);

  const reviewHref = `/review/${workspaceId}/${sprintId}`;

  function replaceDraft(nextDeck: PresentationDeck, preferredSlideId?: string | null) {
    const normalizedDeck = normalizeDeck(nextDeck);
    setDraftDeck(normalizedDeck);
    setDirty(false);
    setSelectedSlideId(() => {
      if (preferredSlideId && normalizedDeck.slides.some((slide) => slide.id === preferredSlideId)) {
        return preferredSlideId;
      }
      if (selectedSlideId && normalizedDeck.slides.some((slide) => slide.id === selectedSlideId)) {
        return selectedSlideId;
      }
      return normalizedDeck.slides[0]?.id ?? null;
    });
  }

  function updateDraft(updater: (deck: PresentationDeck) => PresentationDeck) {
    setDraftDeck((current) => {
      if (!current) {
        return current;
      }
      return normalizeDeck(updater(current));
    });
    setDirty(true);
    setEditorMessage(null);
  }

  function updateCurrentSlide(updater: (slide: PresentationSlide) => PresentationSlide) {
    if (!selectedSlide) {
      return;
    }
    updateDraft((current) => ({
      ...current,
      slides: current.slides.map((slide) => (slide.id === selectedSlide.id ? updater(slide) : slide)),
    }));
  }

  function updateSelectedElement(updates: Partial<PresentationSlideElement>) {
    if (!selectedElementId) {
      return;
    }
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: slide.elements.map((element) => (element.id === selectedElementId ? { ...element, ...updates } : element)),
    }));
  }

  function handleSelectSlide(slideId: string) {
    setSelectedSlideId(slideId);
    setSelectedElementId(null);
  }

  function handleChangeDeckTheme(themeId: string) {
    updateDraft((current) => {
      const theme = resolvePresentationTheme(themeId);
      return applyThemeToDeck({
        ...current,
        theme,
        themeDisplayName: theme.displayName,
        themeId,
      }, theme);
    });
  }

  function handleChangeElementText(elementId: string, text: string) {
    updateCurrentSlide((slide) =>
      synchronizeSlide({
        ...slide,
        elements: slide.elements.map((element) => (element.id === elementId ? { ...element, textContent: text } : element)),
      })
    );
  }

  function handleUpdateElementFrame(
    elementId: string,
    nextFrame: Pick<PresentationSlideElement, "x" | "y" | "width" | "height">
  ) {
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: slide.elements.map((element) => (element.id === elementId ? { ...element, ...nextFrame } : element)),
    }));
  }

  function handleChangeElementFormatting(
    updates: Partial<
      Pick<
        PresentationSlideElement,
        "fontFamily" | "fontSize" | "bold" | "italic" | "underline" | "textAlignment" | "textColor" | "fillColor" | "borderColor" | "borderWidth"
      >
    >
  ) {
    if (!selectedElementId) {
      return;
    }
    updateSelectedElement(updates);
  }

  function handleSelectedElementChange(
    updates: Partial<
      Pick<
        PresentationSlideElement,
        "role" | "textContent" | "fontFamily" | "fontSize" | "bold" | "italic" | "underline" | "textAlignment" | "x" | "y" | "width" | "height" | "fillColor" | "borderColor" | "borderWidth" | "textColor"
      >
    >
  ) {
    if (!selectedElementId) {
      return;
    }
    if (updates.textContent !== undefined) {
      handleChangeElementText(selectedElementId, updates.textContent);
      return;
    }
    if (updates.role !== undefined) {
      handleSetElementRole(updates.role);
      return;
    }
    updateSelectedElement(updates);
  }

  function handleSetElementRole(role: SlideElementRole) {
    if (!selectedElementId || !selectedElement) {
      return;
    }
    updateCurrentSlide((slide) =>
      synchronizeSlide({
        ...slide,
        elements: slide.elements.map((element) =>
          element.id === selectedElementId
            ? {
                ...element,
                role,
                textContent: role === "BODY_BULLETS" ? ensureBulletText(element.textContent) : stripBulletText(element.textContent),
              }
            : element
        ),
      })
    );
  }

  function handleSlideStyleChange(updates: Partial<Pick<PresentationSlide, "backgroundColor" | "showGrid">>) {
    updateCurrentSlide((slide) => ({
      ...slide,
      ...updates,
    }));
  }

  function handleAddTextBox() {
    if (!selectedSlide) {
      return;
    }
    const nextElement = buildTextBox(selectedSlide.id, selectedSlide.elements.length, selectedSlide.elements.length, {
      textContent: "Add your point here",
      role: "FREEFORM",
      x: DEFAULT_TEXTBOX_X + selectedSlide.elements.length * 18,
      y: DEFAULT_TEXTBOX_Y + selectedSlide.elements.length * 18,
      width: DEFAULT_TEXTBOX_WIDTH,
      height: DEFAULT_TEXTBOX_HEIGHT,
      textColor: selectedDeckTheme(draftDeck).colorPalette.textPrimary,
    });
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: normalizeElements([...slide.elements, nextElement]),
    }));
    setSelectedElementId(nextElement.id);
    setEditorMessage("Text box added.");
  }

  function handleAddShape(shapeType: ShapeType) {
    if (!selectedSlide) {
      return;
    }
    const theme = selectedDeckTheme(draftDeck);
    const nextElement = buildShape(selectedSlide.id, selectedSlide.elements.length, selectedSlide.elements.length, shapeType, {
      fillColor: theme.colorPalette.accent + "22",
      borderColor: theme.colorPalette.accent,
      textColor: theme.colorPalette.textPrimary,
      x: 280 + selectedSlide.elements.length * 12,
      y: 160 + selectedSlide.elements.length * 12,
    });
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: normalizeElements([...slide.elements, nextElement]),
    }));
    setSelectedElementId(nextElement.id);
    setEditorMessage("Shape added.");
  }

  function handleDuplicateElement() {
    if (!selectedSlide || !selectedElement) {
      return;
    }
    const nextElement: PresentationSlideElement = {
      ...selectedElement,
      id: crypto.randomUUID(),
      x: Math.min(selectedElement.x + 24, CANVAS_WIDTH - selectedElement.width),
      y: Math.min(selectedElement.y + 24, CANVAS_HEIGHT - selectedElement.height),
      elementOrder: selectedSlide.elements.length,
      zIndex: selectedSlide.elements.length,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: normalizeElements([...slide.elements, nextElement]),
    }));
    setSelectedElementId(nextElement.id);
    setEditorMessage(`${selectedElement.elementType === "SHAPE" ? "Shape" : "Text box"} duplicated.`);
  }

  function handleDeleteElement() {
    if (!selectedElementId) {
      return;
    }
    updateCurrentSlide((slide) =>
      synchronizeSlide({
        ...slide,
        elements: normalizeElements(slide.elements.filter((element) => element.id !== selectedElementId)),
      })
    );
    setSelectedElementId(null);
    setEditorMessage("Object deleted.");
  }

  function handleMoveLayer(direction: "backward" | "forward") {
    if (!selectedSlide || !selectedElementId) {
      return;
    }
    const ordered = normalizeElements(selectedSlide.elements);
    const index = ordered.findIndex((element) => element.id === selectedElementId);
    if (index < 0) {
      return;
    }
    const swapIndex = direction === "forward" ? index + 1 : index - 1;
    if (swapIndex < 0 || swapIndex >= ordered.length) {
      return;
    }
    const next = [...ordered];
    [next[index], next[swapIndex]] = [next[swapIndex], next[index]];
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: reindexElements(next),
    }));
  }

  function handleUpdateNotes(notes: string) {
    updateCurrentSlide((slide) => ({ ...slide, speakerNotes: notes || null }));
  }

  async function handleSave() {
    if (!draftDeck) {
      return;
    }
    try {
      const saved = await saveCurrentDeck(draftDeck);
      replaceDraft(saved, selectedSlide?.id ?? null);
      setEditorMessage("Deck saved.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to save deck.");
    }
  }

  async function saveCurrentDeck(deck: PresentationDeck) {
    return saveDeck.mutateAsync({
      deckId: deck.id,
      payload: toUpdateDeckRequest(deck),
    });
  }

  async function handleExportPowerPoint() {
    if (!draftDeck) {
      return;
    }
    setEditorMessage(null);
    setExportPending(true);
    try {
      let deckForExport = draftDeck;
      if (dirty) {
        const saved = await saveCurrentDeck(draftDeck);
        replaceDraft(saved, selectedSlide?.id ?? null);
        deckForExport = saved;
      }
      const exported = await exportService.exportDeckAsPowerPoint(workspaceId, deckForExport.id);
      downloadBlob(exported.fileName, exported.blob);
      setEditorMessage("PowerPoint exported.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to export PowerPoint.");
    } finally {
      setExportPending(false);
    }
  }

  async function handleAddSlide(payload: AddSlideRequest) {
    if (!draftDeck) {
      return;
    }
    try {
      const nextDeck = await addSlide.mutateAsync({ deckId: draftDeck.id, payload });
      const addedSlide = nextDeck.slides[nextDeck.slides.length - 1] ?? null;
      replaceDraft(nextDeck, addedSlide?.id ?? null);
      setSelectedElementId(null);
      setEditorMessage("Slide added.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to add slide.");
    }
  }

  async function handleDuplicateSlide() {
    if (!draftDeck || !selectedSlide) {
      return;
    }
    try {
      const nextDeck = await duplicateSlide.mutateAsync({ deckId: draftDeck.id, slideId: selectedSlide.id });
      const currentIndex = nextDeck.slides.findIndex((slide) => slide.id === selectedSlide.id);
      const duplicated = nextDeck.slides[currentIndex + 1] ?? nextDeck.slides[currentIndex] ?? nextDeck.slides[0] ?? null;
      replaceDraft(nextDeck, duplicated?.id ?? null);
      setSelectedElementId(null);
      setEditorMessage("Slide duplicated.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to duplicate slide.");
    }
  }

  async function handleDeleteSlide() {
    if (!draftDeck || !selectedSlide || draftDeck.slides.length <= 1) {
      return;
    }
    try {
      const currentIndex = draftDeck.slides.findIndex((slide) => slide.id === selectedSlide.id);
      const fallbackSlideId = draftDeck.slides[currentIndex + 1]?.id ?? draftDeck.slides[currentIndex - 1]?.id ?? null;
      await deleteSlide.mutateAsync({ deckId: draftDeck.id, slideId: selectedSlide.id });
      const nextDeck = {
        ...draftDeck,
        slides: draftDeck.slides
          .filter((slide) => slide.id !== selectedSlide.id)
          .map((slide, index) => ({ ...slide, slideOrder: index })),
      };
      replaceDraft(nextDeck, fallbackSlideId);
      setSelectedElementId(null);
      setEditorMessage("Slide deleted.");
      void deckQuery.refetch();
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to delete slide.");
    }
  }

  async function handleReorderSlides(sourceSlideId: string, targetSlideId: string) {
    if (!draftDeck || sourceSlideId === targetSlideId) {
      return;
    }
    try {
      const nextDeck = await reorderSlides.mutateAsync({
        deckId: draftDeck.id,
        payload: {
          slideIds: reorderSlideIds(
            draftDeck.slides.map((slide) => slide.id),
            sourceSlideId,
            targetSlideId
          ),
        },
      });
      replaceDraft(nextDeck, selectedSlideId);
      setEditorMessage("Slides reordered.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to reorder slides.");
    }
  }

  if (deckQuery.isLoading && !draftDeck) {
    return (
      <Card>
        <p className="font-semibold text-ink">Preparing editor...</p>
        <p className="mt-2 text-sm text-stone-600">Loading the latest saved deck or creating one from the persisted sprint review artifact.</p>
      </Card>
    );
  }

  if (deckQuery.error && !draftDeck) {
    const missingReview = deckQuery.error instanceof ApiError && deckQuery.error.status === 404;
    return (
      <Card>
        <p className="font-semibold text-ink">{missingReview ? "No sprint review available yet" : "Could not load slide deck"}</p>
        <p className="mt-2 text-sm text-stone-600">
          {missingReview
            ? "Generate a sprint review first. The slide editor creates the initial deck from the latest persisted review artifact."
            : deckQuery.error instanceof Error
              ? deckQuery.error.message
              : "Slide editor setup failed."}
        </p>
        <div className="mt-4 flex flex-wrap gap-3">
          <Link href={reviewHref}>
            <Button type="button">Open sprint review</Button>
          </Link>
          <Button onClick={() => void deckQuery.refetch()} type="button" variant="secondary">
            Retry
          </Button>
        </div>
      </Card>
    );
  }

  if (!draftDeck) {
    return null;
  }

  return (
    <div className="flex h-screen flex-col overflow-hidden bg-[#eceff3]">
      {editorMessage ? (
        <div className={editorMessage.toLowerCase().includes("failed") ? "border-b border-rose-200 bg-rose-50 px-4 py-2 text-sm text-rose-700" : "border-b border-slate-200 bg-white px-4 py-2 text-sm text-slate-600"}>{editorMessage}</div>
      ) : null}

      <div className="min-h-0 flex-1">
        <SlideEditorShell
          deck={draftDeck}
          dirty={dirty}
          exportPending={exportPending}
          onAddShape={handleAddShape}
          onAddSlide={handleAddSlide}
          onAddTextBox={handleAddTextBox}
          onChangeElementFormatting={handleChangeElementFormatting}
          onChangeElementText={handleChangeElementText}
          onDeckStatusChange={(value) => updateDraft((current) => ({ ...current, status: value }))}
          onDeckSubtitleChange={(value) => updateDraft((current) => ({ ...current, subtitle: value || null }))}
          onDeckTitleChange={(value) => updateDraft((current) => ({ ...current, title: value }))}
          onDeleteElement={handleDeleteElement}
          onDeleteSlide={() => void handleDeleteSlide()}
          onDuplicateElement={handleDuplicateElement}
          onDuplicateSlide={() => void handleDuplicateSlide()}
          onExportPowerPoint={() => void handleExportPowerPoint()}
          onLayerBackward={() => handleMoveLayer("backward")}
          onLayerForward={() => handleMoveLayer("forward")}
          onReorderSlides={(sourceSlideId, targetSlideId) => void handleReorderSlides(sourceSlideId, targetSlideId)}
          onSave={() => void handleSave()}
          onSelectedElementChange={handleSelectedElementChange}
          onSetElementRole={handleSetElementRole}
          onSelectElement={setSelectedElementId}
          onSelectSlide={handleSelectSlide}
          onSlideStyleChange={handleSlideStyleChange}
          onThemeChange={handleChangeDeckTheme}
          onUpdateElementFrame={handleUpdateElementFrame}
          onUpdateNotes={handleUpdateNotes}
          reviewHref={reviewHref}
          savePending={
            saveDeck.isPending || addSlide.isPending || duplicateSlide.isPending || deleteSlide.isPending || reorderSlides.isPending
          }
          selectedElement={selectedElement}
          selectedSlide={selectedSlide}
        />
      </div>
    </div>
  );
}

function toUpdateDeckRequest(deck: PresentationDeck): UpdateDeckRequest {
  return {
    title: deck.title,
    subtitle: deck.subtitle,
    themeId: deck.themeId,
    status: deck.status,
    slides: deck.slides.map((slide) => ({
      id: slide.id,
      slideType: slide.slideType,
      title: slide.title,
      bulletPoints: slide.bulletPoints,
      bodyText: slide.bodyText,
      speakerNotes: slide.speakerNotes,
      sectionLabel: slide.sectionLabel,
      backgroundColor: slide.backgroundColor,
      backgroundStyleType: slide.backgroundStyleType || "SOLID",
      showGrid: slide.showGrid ?? true,
      layoutType: slide.layoutType,
      templateType: slide.templateType,
      elements: normalizeElements(slide.elements).map((element) => ({
        id: element.id,
        elementType: element.elementType,
        role: element.role,
        textContent: element.textContent,
        x: element.x,
        y: element.y,
        width: element.width,
        height: element.height,
        zIndex: element.zIndex ?? element.elementOrder,
        rotationDegrees: element.rotationDegrees,
        fillColor: element.fillColor,
        borderColor: element.borderColor,
        borderWidth: element.borderWidth,
        textColor: element.textColor,
        fontFamily: element.fontFamily,
        fontSize: element.fontSize,
        bold: element.bold,
        italic: element.italic,
        underline: element.underline,
        textAlignment: element.textAlignment,
        shapeType: element.shapeType,
        hidden: element.hidden ?? false,
      })),
      hidden: slide.hidden,
    })),
  };
}

function synchronizeSlide(slide: PresentationSlide): PresentationSlide {
  const titleElement = slide.elements.find((element) => element.role === "TITLE");
  const sectionElement = slide.elements.find((element) => element.role === "SECTION_LABEL");
  const bodyElement = slide.elements.find((element) => element.role === "BODY" || element.role === "BODY_BULLETS");
  return {
    ...slide,
    elements: normalizeElements(slide.elements),
    title: titleElement?.textContent?.trim() || slide.title || "Untitled slide",
    sectionLabel: sectionElement?.textContent?.trim() || null,
    bodyText: bodyElement?.textContent?.trim() || null,
    bulletPoints: extractBullets(bodyElement?.textContent || ""),
  };
}

function extractBullets(text: string) {
  return text
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line.startsWith("•") || line.startsWith("-") || line.startsWith("*"))
    .map((line) => line.replace(/^[•\-*]\s*/, ""))
    .filter(Boolean);
}

function ensureBulletText(text: string) {
  const lines = text
    .split("\n")
    .map((line) => line.trim())
    .filter(Boolean);
  if (lines.length === 0) {
    return "• Bullet point";
  }
  return lines.map((line) => (line.startsWith("•") || line.startsWith("-") || line.startsWith("*") ? line.replace(/^[\-*]\s*/, "• ") : `• ${line}`)).join("\n");
}

function stripBulletText(text: string) {
  return text
    .split("\n")
    .map((line) => line.replace(/^[•\-*]\s*/, ""))
    .join("\n");
}

function buildTextBox(
  slideId: string,
  elementOrder: number,
  zIndex: number,
  overrides: {
    textContent: string;
    role: SlideElementRole;
    x: number;
    y: number;
    width: number;
    height: number;
    textColor?: string;
    fontFamily?: string;
    fontSize?: number;
    bold?: boolean;
    italic?: boolean;
    underline?: boolean;
    textAlignment?: PresentationSlideElement["textAlignment"];
  }
): PresentationSlideElement {
  const now = new Date().toISOString();
  return {
    id: crypto.randomUUID(),
    slideId,
    elementOrder,
    elementType: "TEXT_BOX",
    role: overrides.role,
    textContent: overrides.textContent,
    x: overrides.x,
    y: overrides.y,
    width: overrides.width,
    height: overrides.height,
    zIndex,
    rotationDegrees: 0,
    fillColor: "transparent",
    borderColor: "transparent",
    borderWidth: 0,
    textColor: overrides.textColor || "#0f172a",
    fontFamily: overrides.fontFamily || "Aptos",
    fontSize: overrides.fontSize || 24,
    bold: overrides.bold ?? false,
    italic: overrides.italic ?? false,
    underline: overrides.underline ?? false,
    textAlignment: overrides.textAlignment || "LEFT",
    hidden: false,
    createdAt: now,
    updatedAt: now,
  };
}

function buildShape(
  slideId: string,
  elementOrder: number,
  zIndex: number,
  shapeType: ShapeType,
  overrides?: Partial<PresentationSlideElement>
): PresentationSlideElement {
  const now = new Date().toISOString();
  return {
    id: crypto.randomUUID(),
    slideId,
    elementOrder,
    elementType: "SHAPE",
    role: "FREEFORM",
    textContent: overrides?.textContent || "",
    x: overrides?.x ?? 320,
    y: overrides?.y ?? 160,
    width: overrides?.width ?? (shapeType === "LINE" ? 240 : 220),
    height: overrides?.height ?? (shapeType === "LINE" ? 24 : 140),
    zIndex,
    rotationDegrees: overrides?.rotationDegrees ?? 0,
    fillColor: overrides?.fillColor || "#dbeafe",
    borderColor: overrides?.borderColor || "#2563eb",
    borderWidth: overrides?.borderWidth ?? 2,
    textColor: overrides?.textColor || "#0f172a",
    fontFamily: overrides?.fontFamily || "Aptos",
    fontSize: overrides?.fontSize || 22,
    bold: overrides?.bold ?? false,
    italic: overrides?.italic ?? false,
    underline: overrides?.underline ?? false,
    textAlignment: overrides?.textAlignment || "CENTER",
    shapeType,
    hidden: false,
    createdAt: now,
    updatedAt: now,
  };
}

function reorderSlideIds(slideIds: string[], sourceSlideId: string, targetSlideId: string) {
  const next = [...slideIds];
  const sourceIndex = next.indexOf(sourceSlideId);
  const targetIndex = next.indexOf(targetSlideId);
  if (sourceIndex < 0 || targetIndex < 0) {
    return next;
  }
  const [moved] = next.splice(sourceIndex, 1);
  next.splice(targetIndex, 0, moved);
  return next;
}

function normalizeDeck(deck: PresentationDeck): PresentationDeck {
  const theme = deck.theme || resolvePresentationTheme(deck.themeId);
  return {
    ...deck,
    theme,
    themeDisplayName: deck.themeDisplayName || theme.displayName,
    themeId: deck.themeId || theme.themeId,
    slides: deck.slides.map((slide, index) => ({
      ...slide,
      backgroundStyleType: slide.backgroundStyleType || "SOLID",
      showGrid: slide.showGrid ?? true,
      slideOrder: index,
      elements: normalizeElements(slide.elements),
    })),
  };
}

function normalizeElements(elements: PresentationSlideElement[]) {
  return [...elements]
    .sort((left, right) => (left.zIndex ?? left.elementOrder) - (right.zIndex ?? right.elementOrder))
    .map((element, index) => normalizeElement(element, index));
}

function selectedDeckTheme(deck: PresentationDeck | null) {
  return resolvePresentationTheme(deck?.themeId || deck?.theme?.themeId);
}

function reindexElements(elements: PresentationSlideElement[]) {
  return elements.map((element, index) => normalizeElement(element, index));
}

function normalizeElement(element: PresentationSlideElement, index: number): PresentationSlideElement {
  return {
    ...element,
    borderWidth: element.borderWidth ?? (element.elementType === "SHAPE" ? 2 : 0),
    elementOrder: index,
    hidden: element.hidden ?? false,
    textAlignment: element.textAlignment || "LEFT",
    underline: element.underline ?? false,
    zIndex: index,
  };
}

function applyThemeToDeck(deck: PresentationDeck, theme = resolvePresentationTheme(deck.themeId || deck.theme?.themeId)) {
  return {
    ...deck,
    theme,
    themeDisplayName: theme.displayName,
    themeId: theme.themeId,
    slides: deck.slides.map((slide) => ({
      ...slide,
      backgroundColor: theme.colorPalette.background,
      elements: normalizeElements(
        slide.elements.map((element) => {
          if (element.elementType === "SHAPE") {
            return {
              ...element,
              fillColor: theme.colorPalette.accent + "22",
              borderColor: theme.colorPalette.accent,
              textColor: theme.colorPalette.textPrimary,
              fontFamily: theme.typography.bodyFontFamily,
              fontSize: element.fontSize || theme.typography.bodyFontSize,
            };
          }

          const nextRole = element.role;
          const isTitle = nextRole === "TITLE";
          const isSubtitle = nextRole === "SUBTITLE" || nextRole === "SECTION_LABEL";
          const isSmall = nextRole === "FOOTER";

          return {
            ...element,
            fillColor: "transparent",
            borderColor: "transparent",
            textColor: isTitle ? theme.colorPalette.textPrimary : isSubtitle ? theme.colorPalette.textSecondary : theme.colorPalette.textPrimary,
            fontFamily: isTitle ? theme.typography.titleFontFamily : theme.typography.bodyFontFamily,
            fontSize: isTitle
              ? theme.typography.titleFontSize
              : isSubtitle
                ? theme.typography.subtitleFontSize
                : isSmall
                  ? theme.typography.smallFontSize
                  : theme.typography.bodyFontSize,
          };
        })
      ),
    })),
  };
}
