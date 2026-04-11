import { DeckHeader } from "@/components/slides/deck-header";
import { SlideCanvasPreview } from "@/components/slides/slide-canvas-preview";
import { SlideNotesEditor } from "@/components/slides/slide-notes-editor";
import { SlidePropertiesPanel } from "@/components/slides/slide-properties-panel";
import { SlideSidebar } from "@/components/slides/slide-sidebar";
import { SlideToolbar } from "@/components/slides/slide-toolbar";
import type {
  AddSlideRequest,
  DeckStatus,
  PresentationDeck,
  PresentationSlide,
  PresentationSlideElement,
  SlideElementRole,
} from "@/types/presentation";

export function SlideEditorLayout({
  deck,
  selectedSlide,
  selectedElement,
  previewMode,
  dirty,
  structuralBusy,
  savePending,
  sprintHref,
  onDeckTitleChange,
  onDeckSubtitleChange,
  onDeckStatusChange,
  onSelectSlide,
  onAddSlide,
  onDeleteSlide,
  onDuplicateSlide,
  onReorderSlides,
  onSave,
  onSlideChange,
  onSelectElement,
  onElementChange,
  onElementRoleChange,
  onAddTextBox,
  onDeleteElement,
  onTogglePreviewMode,
}: {
  deck: PresentationDeck;
  selectedSlide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  previewMode: boolean;
  dirty: boolean;
  structuralBusy?: boolean;
  savePending?: boolean;
  sprintHref: string;
  onDeckTitleChange: (value: string) => void;
  onDeckSubtitleChange: (value: string) => void;
  onDeckStatusChange: (value: DeckStatus) => void;
  onSelectSlide: (slideId: string) => void;
  onAddSlide: (payload: AddSlideRequest) => void;
  onDeleteSlide: () => void;
  onDuplicateSlide: () => void;
  onReorderSlides: (sourceSlideId: string, targetSlideId: string) => void;
  onSave: () => void;
  onSlideChange: (updater: (slide: PresentationSlide) => PresentationSlide) => void;
  onSelectElement: (elementId: string | null) => void;
  onElementChange: (patch: Partial<PresentationSlideElement>) => void;
  onElementRoleChange: (role: SlideElementRole) => void;
  onAddTextBox: () => void;
  onDeleteElement: () => void;
  onTogglePreviewMode: () => void;
}) {
  return (
    <div className="space-y-6">
      <DeckHeader
        dirty={dirty}
        disabled={structuralBusy || savePending}
        onStatusChange={onDeckStatusChange}
        onSubtitleChange={onDeckSubtitleChange}
        onTitleChange={onDeckTitleChange}
        status={deck.status}
        subtitle={deck.subtitle}
        title={deck.title}
      />

      <SlideToolbar
        canDelete={Boolean(selectedSlide)}
        canDuplicate={Boolean(selectedSlide)}
        deletePending={structuralBusy}
        duplicatePending={structuralBusy}
        isDirty={dirty}
        onAddTextBox={onAddTextBox}
        onDelete={onDeleteSlide}
        onDeleteElement={onDeleteElement}
        onDuplicate={onDuplicateSlide}
        onElementChange={onElementChange}
        onElementRoleChange={onElementRoleChange}
        onSave={onSave}
        onTogglePreviewMode={onTogglePreviewMode}
        previewMode={previewMode}
        savePending={savePending}
        selectedElement={selectedElement}
        sprintHref={sprintHref}
      />

      <div className="grid gap-6 xl:grid-cols-[18rem_minmax(0,1fr)_22rem]">
        {!previewMode ? (
          <SlideSidebar
            disabled={structuralBusy || savePending}
            onAddSlide={onAddSlide}
            onReorderSlides={onReorderSlides}
            onSelectSlide={onSelectSlide}
            selectedSlideId={selectedSlide?.id ?? null}
            slides={deck.slides}
          />
        ) : null}

        <div className="space-y-6">
          <SlideCanvasPreview
            deckSubtitle={deck.subtitle}
            deckTitle={deck.title}
            onSelectElement={onSelectElement}
            onUpdateElement={(elementId, updater) => onSlideChange((slide) => ({
              ...slide,
              elements: slide.elements.map((element) => (element.id === elementId ? updater(element) : element)),
            }))}
            previewMode={previewMode}
            selectedElementId={selectedElement?.id ?? null}
            slide={selectedSlide}
          />
          {!previewMode ? (
            <SlideNotesEditor disabled={savePending} onUpdate={onSlideChange} slide={selectedSlide} />
          ) : null}
        </div>

        {!previewMode ? (
          <SlidePropertiesPanel
            disabled={savePending}
            onElementChange={onElementChange}
            onSlideChange={onSlideChange}
            selectedElement={selectedElement}
            slide={selectedSlide}
          />
        ) : null}
      </div>
    </div>
  );
}
