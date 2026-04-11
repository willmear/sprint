import { AddTextBoxButton } from "@/components/slides/add-text-box-button";
import { BoldToggleButton } from "@/components/slides/bold-toggle-button";
import { ElementActions } from "@/components/slides/element-actions";
import { FontFamilySelect } from "@/components/slides/font-family-select";
import { FontSizeInput } from "@/components/slides/font-size-input";
import { ItalicToggleButton } from "@/components/slides/italic-toggle-button";
import { TextAlignButtons } from "@/components/slides/text-align-buttons";
import type { PresentationSlideElement } from "@/types/presentation";

export function SlideTopToolbar({
  selectedElement,
  onAddTextBox,
  onDuplicateElement,
  onDeleteElement,
  onChangeFormatting,
}: {
  selectedElement: PresentationSlideElement | null;
  onAddTextBox: () => void;
  onDuplicateElement: () => void;
  onDeleteElement: () => void;
  onChangeFormatting: (updates: Partial<Pick<PresentationSlideElement, "fontFamily" | "fontSize" | "bold" | "italic" | "textAlignment">>) => void;
}) {
  const disabled = !selectedElement;

  return (
    <div className="rounded-[28px] border border-line bg-white px-4 py-3 shadow-panel">
      <div className="flex flex-wrap items-center gap-3">
        <AddTextBoxButton onClick={onAddTextBox} />
        <ElementActions canAct={!disabled} onDelete={onDeleteElement} onDuplicate={onDuplicateElement} />
        <div className="h-8 w-px bg-line" />
        <FontFamilySelect
          disabled={disabled}
          onChange={(value) => onChangeFormatting({ fontFamily: value })}
          value={selectedElement?.fontFamily || "Aptos"}
        />
        <FontSizeInput
          disabled={disabled}
          onChange={(value) => {
            if (!Number.isFinite(value)) {
              return;
            }
            onChangeFormatting({ fontSize: Math.min(144, Math.max(8, value)) });
          }}
          value={selectedElement?.fontSize || 24}
        />
        <div className="h-8 w-px bg-line" />
        <BoldToggleButton
          active={selectedElement?.bold ?? false}
          disabled={disabled}
          onToggle={() => onChangeFormatting({ bold: !selectedElement?.bold })}
        />
        <ItalicToggleButton
          active={selectedElement?.italic ?? false}
          disabled={disabled}
          onToggle={() => onChangeFormatting({ italic: !selectedElement?.italic })}
        />
        <TextAlignButtons
          disabled={disabled}
          onChange={(value) => onChangeFormatting({ textAlignment: value })}
          value={selectedElement?.textAlignment ?? null}
        />
        <div className="ml-auto rounded-full bg-stone-100 px-3 py-1 text-xs font-medium text-stone-600">
          {selectedElement ? "Formatting selected text box" : "Select a text box to format it"}
        </div>
      </div>
    </div>
  );
}
