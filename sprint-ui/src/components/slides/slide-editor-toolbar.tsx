import { AddTextBoxButton } from "@/components/slides/add-text-box-button";
import { BoldToggleButton } from "@/components/slides/bold-toggle-button";
import { ElementActions } from "@/components/slides/element-actions";
import { FontFamilySelect } from "@/components/slides/font-family-select";
import { FontSizeInput } from "@/components/slides/font-size-input";
import { ItalicToggleButton } from "@/components/slides/italic-toggle-button";
import { TextAlignButtons } from "@/components/slides/text-align-buttons";
import type { PresentationSlideElement } from "@/types/presentation";

function ToolbarGroup({ children }: { children: React.ReactNode }) {
  return <div className="flex items-center gap-2 rounded-md border border-slate-200 bg-white px-2 py-1.5">{children}</div>;
}

export function SlideEditorToolbar({
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
    <div className="border-b border-slate-200 bg-[#f7f8fa] px-4 py-2">
      <div className="flex flex-wrap items-center gap-2">
        <ToolbarGroup>
          <AddTextBoxButton onClick={onAddTextBox} />
          <ElementActions canAct={!disabled} onDelete={onDeleteElement} onDuplicate={onDuplicateElement} />
        </ToolbarGroup>

        <ToolbarGroup>
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
        </ToolbarGroup>

        <ToolbarGroup>
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
        </ToolbarGroup>

        <div className="ml-auto rounded-md border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-500">
          {selectedElement ? "Selected text box" : "Select an object to format"}
        </div>
      </div>
    </div>
  );
}
