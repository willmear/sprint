import Link from "next/link";

import { Button } from "@/components/ui/button";
import type { PresentationSlideElement, SlideElementRole, TextAlignment } from "@/types/presentation";

const fonts = ["Aptos", "Arial", "Georgia", "Times New Roman", "Menlo"];
const alignments: TextAlignment[] = ["LEFT", "CENTER", "RIGHT"];
const roles: SlideElementRole[] = ["TITLE", "BODY", "SECTION_LABEL", "FREEFORM"];

export function SlideToolbar({
  selectedElement,
  canDelete,
  canDuplicate,
  deletePending,
  duplicatePending,
  isDirty,
  previewMode,
  savePending,
  sprintHref,
  onAddTextBox,
  onDeleteElement,
  onDelete,
  onDuplicate,
  onElementChange,
  onElementRoleChange,
  onSave,
  onTogglePreviewMode,
}: {
  selectedElement: PresentationSlideElement | null;
  canDelete: boolean;
  canDuplicate: boolean;
  deletePending?: boolean;
  duplicatePending?: boolean;
  isDirty: boolean;
  previewMode: boolean;
  savePending?: boolean;
  sprintHref: string;
  onAddTextBox: () => void;
  onDeleteElement: () => void;
  onDelete: () => void;
  onDuplicate: () => void;
  onElementChange: (patch: Partial<PresentationSlideElement>) => void;
  onElementRoleChange: (role: SlideElementRole) => void;
  onSave: () => void;
  onTogglePreviewMode: () => void;
}) {
  return (
    <div className="space-y-3 rounded-[2rem] border border-line bg-white p-4 shadow-panel">
      <div className="flex flex-wrap items-center gap-3">
        <Button disabled={!isDirty || savePending} onClick={onSave} type="button">
          {savePending ? "Saving..." : "Save deck"}
        </Button>
        <Button onClick={onAddTextBox} type="button" variant="secondary">
          Add text box
        </Button>
        <Button disabled={!selectedElement} onClick={onDeleteElement} type="button" variant="secondary">
          Delete text box
        </Button>
        <Button disabled={!canDuplicate || duplicatePending} onClick={onDuplicate} type="button" variant="secondary">
          {duplicatePending ? "Duplicating..." : "Duplicate slide"}
        </Button>
        <Button disabled={!canDelete || deletePending} onClick={onDelete} type="button" variant="secondary">
          {deletePending ? "Deleting..." : "Delete slide"}
        </Button>
        <Button onClick={onTogglePreviewMode} type="button" variant="secondary">
          {previewMode ? "Edit mode" : "Preview mode"}
        </Button>
        <Button disabled type="button" variant="ghost">
          Export PowerPoint soon
        </Button>
        <Link href={sprintHref}>
          <Button type="button" variant="ghost">Open sprint review</Button>
        </Link>
      </div>

      <div className="flex flex-wrap items-center gap-3 rounded-3xl border border-line bg-cloud px-3 py-3">
        <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Text formatting</p>
        <select
          className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          disabled={!selectedElement}
          onChange={(event) => onElementChange({ fontFamily: event.target.value })}
          value={selectedElement?.fontFamily || fonts[0]}
        >
          {fonts.map((font) => (
            <option key={font} value={font}>{font}</option>
          ))}
        </select>
        <input
          className="w-20 rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          disabled={!selectedElement}
          min={10}
          onChange={(event) => onElementChange({ fontSize: Number(event.target.value) || 18 })}
          type="number"
          value={selectedElement?.fontSize || 18}
        />
        <Button
          disabled={!selectedElement}
          onClick={() => onElementChange({ bold: !selectedElement?.bold })}
          type="button"
          variant={selectedElement?.bold ? "default" : "secondary"}
        >
          Bold
        </Button>
        <Button
          disabled={!selectedElement}
          onClick={() => onElementChange({ italic: !selectedElement?.italic })}
          type="button"
          variant={selectedElement?.italic ? "default" : "secondary"}
        >
          Italic
        </Button>
        <div className="flex gap-2">
          {alignments.map((alignment) => (
            <Button
              key={alignment}
              disabled={!selectedElement}
              onClick={() => onElementChange({ textAlignment: alignment })}
              type="button"
              variant={selectedElement?.textAlignment === alignment ? "default" : "secondary"}
            >
              {alignment.toLowerCase()}
            </Button>
          ))}
        </div>
        <select
          className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          disabled={!selectedElement}
          onChange={(event) => onElementRoleChange(event.target.value as SlideElementRole)}
          value={selectedElement?.role || "FREEFORM"}
        >
          {roles.map((role) => (
            <option key={role} value={role}>{role.replaceAll("_", " ")}</option>
          ))}
        </select>
      </div>
    </div>
  );
}
