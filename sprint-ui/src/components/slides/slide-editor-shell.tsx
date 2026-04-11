import { SlideEditorTopBar } from "@/components/slides/slide-editor-top-bar";
import { SlideEditorToolbar } from "@/components/slides/slide-editor-toolbar";
import { SlideInspectorPanel } from "@/components/slides/slide-inspector-panel";
import { SlideThumbnailPane } from "@/components/slides/slide-thumbnail-pane";
import { SlideWorkspace } from "@/components/slides/slide-workspace";
import type { AddSlideRequest, DeckStatus, PresentationDeck, PresentationSlide, PresentationSlideElement } from "@/types/presentation";

export function SlideEditorShell({
  deck,
  selectedSlide,
  selectedElement,
  dirty,
  savePending,
  reviewHref,
  onDeckTitleChange,
  onDeckSubtitleChange,
  onDeckStatusChange,
  onSave,
  onSelectSlide,
  onAddSlide,
  onDuplicateSlide,
  onDeleteSlide,
  onReorderSlides,
  onSelectElement,
  onAddTextBox,
  onDuplicateElement,
  onDeleteElement,
  onChangeElementText,
  onUpdateElementFrame,
  onChangeElementFormatting,
  onUpdateNotes,
}: {
  deck: PresentationDeck;
  selectedSlide: PresentationSlide | null;
  selectedElement: PresentationSlideElement | null;
  dirty: boolean;
  savePending?: boolean;
  reviewHref: string;
  onDeckTitleChange: (value: string) => void;
  onDeckSubtitleChange: (value: string) => void;
  onDeckStatusChange: (value: DeckStatus) => void;
  onSave: () => void;
  onSelectSlide: (slideId: string) => void;
  onAddSlide: (payload: AddSlideRequest) => void;
  onDuplicateSlide: () => void;
  onDeleteSlide: () => void;
  onReorderSlides: (sourceSlideId: string, targetSlideId: string) => void;
  onSelectElement: (elementId: string | null) => void;
  onAddTextBox: () => void;
  onDuplicateElement: () => void;
  onDeleteElement: () => void;
  onChangeElementText: (elementId: string, text: string) => void;
  onUpdateElementFrame: (elementId: string, nextFrame: Pick<PresentationSlideElement, "x" | "y" | "width" | "height">) => void;
  onChangeElementFormatting: (updates: Partial<Pick<PresentationSlideElement, "fontFamily" | "fontSize" | "bold" | "italic" | "textAlignment">>) => void;
  onUpdateNotes: (notes: string) => void;
}) {
  return (
    <div className="flex min-h-screen flex-col overflow-hidden bg-[#f3f4f6]">
      <SlideEditorTopBar
        dirty={dirty}
        onSave={onSave}
        onStatusChange={onDeckStatusChange}
        onSubtitleChange={onDeckSubtitleChange}
        onTitleChange={onDeckTitleChange}
        reviewHref={reviewHref}
        savePending={savePending}
        status={deck.status}
        subtitle={deck.subtitle}
        title={deck.title}
      />

      <SlideEditorToolbar
        onAddTextBox={onAddTextBox}
        onChangeFormatting={onChangeElementFormatting}
        onDeleteElement={onDeleteElement}
        onDuplicateElement={onDuplicateElement}
        selectedElement={selectedElement}
      />

      <div className="grid min-h-0 flex-1 grid-cols-[260px_minmax(0,1fr)_300px]">
        <SlideThumbnailPane
          onAddSlide={onAddSlide}
          onDeleteSlide={onDeleteSlide}
          onDuplicateSlide={onDuplicateSlide}
          onReorderSlides={onReorderSlides}
          onSelectSlide={onSelectSlide}
          selectedSlide={selectedSlide}
          slides={deck.slides}
        />

        <SlideWorkspace
          deckSubtitle={deck.subtitle}
          deckTitle={deck.title}
          onChangeElementText={onChangeElementText}
          onSelectElement={onSelectElement}
          onUpdateElementFrame={onUpdateElementFrame}
          selectedElementId={selectedElement?.id ?? null}
          slide={selectedSlide}
        />

        <SlideInspectorPanel
          onUpdateNotes={onUpdateNotes}
          selectedElement={selectedElement}
          slide={selectedSlide}
        />
      </div>
    </div>
  );
}
