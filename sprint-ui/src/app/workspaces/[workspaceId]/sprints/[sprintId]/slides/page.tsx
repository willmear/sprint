"use client";

import Link from "next/link";
import { use, useEffect, useMemo, useState } from "react";

import { SlideEditorShell } from "@/components/slides/slide-editor-shell";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ApiError } from "@/lib/api/client";
import {
  useAddSlide,
  useDeleteSlide,
  useDuplicateSlide,
  usePresentationDeck,
  useReorderSlides,
  useSavePresentationDeck,
} from "@/lib/hooks/use-presentation";
import type {
  AddSlideRequest,
  PresentationDeck,
  PresentationSlide,
  PresentationSlideElement,
  SlideElementRole,
  UpdateDeckRequest,
} from "@/types/presentation";

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

  useEffect(() => {
    if (!deckQuery.data) {
      return;
    }
    setDraftDeck(deckQuery.data);
    setDirty(false);
    setSelectedSlideId((current) => {
      if (current && deckQuery.data.slides.some((slide) => slide.id === current)) {
        return current;
      }
      return deckQuery.data.slides[0]?.id ?? null;
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
    setDraftDeck(nextDeck);
    setDirty(false);
    setSelectedSlideId(() => {
      if (preferredSlideId && nextDeck.slides.some((slide) => slide.id === preferredSlideId)) {
        return preferredSlideId;
      }
      if (selectedSlideId && nextDeck.slides.some((slide) => slide.id === selectedSlideId)) {
        return selectedSlideId;
      }
      return nextDeck.slides[0]?.id ?? null;
    });
  }

  function updateDraft(updater: (deck: PresentationDeck) => PresentationDeck) {
    setDraftDeck((current) => {
      if (!current) {
        return current;
      }
      return updater(current);
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

  function handleSelectSlide(slideId: string) {
    setSelectedSlideId(slideId);
    setSelectedElementId(null);
  }

  function handleChangeElementText(elementId: string, text: string) {
    updateCurrentSlide((slide) => {
      const nextElements = slide.elements.map((element) => (element.id === elementId ? { ...element, textContent: text } : element));
      return synchronizeSlide({ ...slide, elements: nextElements });
    });
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
    updates: Partial<Pick<PresentationSlideElement, "fontFamily" | "fontSize" | "bold" | "italic" | "textAlignment">>
  ) {
    if (!selectedElementId) {
      return;
    }
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: slide.elements.map((element) => (element.id === selectedElementId ? { ...element, ...updates } : element)),
    }));
  }

  function handleAddTextBox() {
    if (!selectedSlide) {
      return;
    }
    const offset = selectedSlide.elements.length * 18;
    const nextElement = buildTextBox(selectedSlide.id, selectedSlide.elements.length, {
      textContent: "Add your point here",
      role: "FREEFORM",
      x: DEFAULT_TEXTBOX_X + offset,
      y: DEFAULT_TEXTBOX_Y + offset,
      width: DEFAULT_TEXTBOX_WIDTH,
      height: DEFAULT_TEXTBOX_HEIGHT,
    });
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: [...slide.elements, nextElement],
    }));
    setSelectedElementId(nextElement.id);
    setEditorMessage("Text box added.");
  }

  function handleDuplicateElement() {
    if (!selectedSlide || !selectedElement) {
      return;
    }
    const nextElement = buildTextBox(selectedSlide.id, selectedSlide.elements.length, {
      textContent: `${selectedElement.textContent}`,
      role: selectedElement.role,
      x: Math.min(selectedElement.x + 24, 1280 - selectedElement.width),
      y: Math.min(selectedElement.y + 24, 720 - selectedElement.height),
      width: selectedElement.width,
      height: selectedElement.height,
      fontFamily: selectedElement.fontFamily,
      fontSize: selectedElement.fontSize,
      bold: selectedElement.bold,
      italic: selectedElement.italic,
      textAlignment: selectedElement.textAlignment,
    });
    updateCurrentSlide((slide) => ({
      ...slide,
      elements: [...slide.elements, nextElement],
    }));
    setSelectedElementId(nextElement.id);
    setEditorMessage("Text box duplicated.");
  }

  function handleDeleteElement() {
    if (!selectedElementId) {
      return;
    }
    updateCurrentSlide((slide) =>
      synchronizeSlide({
        ...slide,
        elements: slide.elements.filter((element) => element.id !== selectedElementId),
      })
    );
    setSelectedElementId(null);
    setEditorMessage("Text box deleted.");
  }


  function handleUpdateNotes(notes: string) {
    updateCurrentSlide((slide) => ({ ...slide, speakerNotes: notes || null }));
  }

  async function handleSave() {
    if (!draftDeck) {
      return;
    }
    try {
      const saved = await saveDeck.mutateAsync({
        deckId: draftDeck.id,
        payload: toUpdateDeckRequest(draftDeck),
      });
      replaceDraft(saved, selectedSlide?.id ?? null);
      setEditorMessage("Deck saved.");
    } catch (error) {
      setEditorMessage(error instanceof Error ? error.message : "Failed to save deck.");
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
    const reorderedIds = reorderSlideIds(
      draftDeck.slides.map((slide) => slide.id),
      sourceSlideId,
      targetSlideId
    );
    try {
      const nextDeck = await reorderSlides.mutateAsync({
        deckId: draftDeck.id,
        payload: { slideIds: reorderedIds },
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
    <div className="min-h-screen bg-[#eceff3]">
      {editorMessage ? (
        <div className={editorMessage.toLowerCase().includes("failed") ? "border-b border-rose-200 bg-rose-50 px-4 py-2 text-sm text-rose-700" : "border-b border-slate-200 bg-white px-4 py-2 text-sm text-slate-600"}>{editorMessage}</div>
      ) : null}

      <SlideEditorShell
        deck={draftDeck}
        dirty={dirty}
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
        onReorderSlides={(sourceSlideId, targetSlideId) => void handleReorderSlides(sourceSlideId, targetSlideId)}
        onSave={() => void handleSave()}
        onUpdateNotes={handleUpdateNotes}
        onSelectElement={setSelectedElementId}
        onSelectSlide={handleSelectSlide}
        onSlideChange={updateCurrentSlide}
        onUpdateElementFrame={handleUpdateElementFrame}
        reviewHref={reviewHref}
        savePending={
          saveDeck.isPending || addSlide.isPending || duplicateSlide.isPending || deleteSlide.isPending || reorderSlides.isPending
        }
        selectedElement={selectedElement}
        selectedSlide={selectedSlide}
      />
    </div>
  );
}

function toUpdateDeckRequest(deck: PresentationDeck): UpdateDeckRequest {
  return {
    title: deck.title,
    subtitle: deck.subtitle,
    status: deck.status,
    slides: deck.slides.map((slide) => ({
      id: slide.id,
      slideType: slide.slideType,
      title: slide.title,
      bulletPoints: slide.bulletPoints,
      bodyText: slide.bodyText,
      speakerNotes: slide.speakerNotes,
      sectionLabel: slide.sectionLabel,
      layoutType: slide.layoutType,
      elements: slide.elements.map((element) => ({
        id: element.id,
        elementType: element.elementType,
        role: element.role,
        textContent: element.textContent,
        x: element.x,
        y: element.y,
        width: element.width,
        height: element.height,
        fontFamily: element.fontFamily,
        fontSize: element.fontSize,
        bold: element.bold,
        italic: element.italic,
        textAlignment: element.textAlignment,
      })),
      hidden: slide.hidden,
    })),
  };
}

function synchronizeSlide(slide: PresentationSlide): PresentationSlide {
  const titleElement = slide.elements.find((element) => element.role === "TITLE");
  const sectionElement = slide.elements.find((element) => element.role === "SECTION_LABEL");
  const bodyElement = slide.elements.find((element) => element.role === "BODY");
  return {
    ...slide,
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

function buildTextBox(
  slideId: string,
  elementOrder: number,
  overrides: {
    textContent: string;
    role: SlideElementRole;
    x: number;
    y: number;
    width: number;
    height: number;
    fontFamily?: string;
    fontSize?: number;
    bold?: boolean;
    italic?: boolean;
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
    fontFamily: overrides.fontFamily || "Aptos",
    fontSize: overrides.fontSize || 24,
    bold: overrides.bold ?? false,
    italic: overrides.italic ?? false,
    textAlignment: overrides.textAlignment || "LEFT",
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
