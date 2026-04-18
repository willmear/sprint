import { SlideEditorTopBar } from "@/components/slides/slide-editor-top-bar";
import { SlideEditorToolbar } from "@/components/slides/slide-editor-toolbar";
import { SlideInspectorPanel } from "@/components/slides/slide-inspector-panel";
import { SlideThumbnailPane } from "@/components/slides/slide-thumbnail-pane";
import { SlideWorkspace } from "@/components/slides/slide-workspace";
import type { AddSlideRequest, DeckStatus, PresentationDeck, PresentationSlide, PresentationSlideElement, ShapeType } from "@/types/presentation";

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
  onThemeChange,
  onSelectSlide,
  onAddSlide,
  onDuplicateSlide,
  onDeleteSlide,
  onReorderSlides,
  onSelectElement,
  onAddTextBox,
  onAddShape,
  onDuplicateElement,
  onDeleteElement,
  onLayerBackward,
  onLayerForward,
  onChangeElementText,
  onUpdateElementFrame,
  onChangeElementFormatting,
  onSelectedElementChange,
  onSlideStyleChange,
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
  onThemeChange: (value: string) => void;
  onSelectSlide: (slideId: string) => void;
  onAddSlide: (payload: AddSlideRequest) => void;
  onDuplicateSlide: () => void;
  onDeleteSlide: () => void;
  onReorderSlides: (sourceSlideId: string, targetSlideId: string) => void;
  onSelectElement: (elementId: string | null) => void;
  onAddTextBox: () => void;
  onAddShape: (shapeType: ShapeType) => void;
  onDuplicateElement: () => void;
  onDeleteElement: () => void;
  onLayerBackward: () => void;
  onLayerForward: () => void;
  onChangeElementText: (elementId: string, text: string) => void;
  onUpdateElementFrame: (elementId: string, nextFrame: Pick<PresentationSlideElement, "x" | "y" | "width" | "height">) => void;
  onChangeElementFormatting: (
    updates: Partial<
      Pick<
        PresentationSlideElement,
        "fontFamily" | "fontSize" | "bold" | "italic" | "underline" | "textAlignment" | "textColor" | "fillColor" | "borderColor" | "borderWidth"
      >
    >
  ) => void;
  onSelectedElementChange: (
    updates: Partial<Pick<PresentationSlideElement, "textContent" | "x" | "y" | "width" | "height" | "fillColor" | "borderColor" | "borderWidth" | "textColor">>
  ) => void;
  onSlideStyleChange: (updates: Partial<Pick<PresentationSlide, "backgroundColor" | "showGrid">>) => void;
  onUpdateNotes: (notes: string) => void;
}) {
  return (
    <div className="flex h-full min-h-0 flex-col overflow-hidden bg-[#f3f4f6]">
      <SlideEditorTopBar
        dirty={dirty}
        onSave={onSave}
        onStatusChange={onDeckStatusChange}
        onSubtitleChange={onDeckSubtitleChange}
        onThemeChange={onThemeChange}
        onTitleChange={onDeckTitleChange}
        reviewHref={reviewHref}
        savePending={savePending}
        status={deck.status}
        subtitle={deck.subtitle}
        themeId={deck.themeId}
        title={deck.title}
      />

      <SlideEditorToolbar
        onAddShape={onAddShape}
        onAddTextBox={onAddTextBox}
        onChangeFormatting={onChangeElementFormatting}
        onDeleteElement={onDeleteElement}
        onDuplicateElement={onDuplicateElement}
        onLayerBackward={onLayerBackward}
        onLayerForward={onLayerForward}
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
          deckTheme={deck.theme}
          deckTitle={deck.title}
          onChangeElementText={onChangeElementText}
          onSelectElement={onSelectElement}
          onUpdateElementFrame={onUpdateElementFrame}
          selectedElementId={selectedElement?.id ?? null}
          slide={selectedSlide}
        />

        <SlideInspectorPanel
          deckTheme={deck.theme}
          onSelectedElementChange={onSelectedElementChange}
          onSlideStyleChange={onSlideStyleChange}
          onUpdateNotes={onUpdateNotes}
          selectedElement={selectedElement}
          slide={selectedSlide}
        />
      </div>
    </div>
  );
}
