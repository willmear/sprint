import { useState } from "react";

import { Button } from "@/components/ui/button";
import type { AddSlideRequest, SlideLayoutType, SlideType } from "@/types/presentation";

const slideTypes: SlideType[] = ["CUSTOM", "OVERVIEW", "THEMES", "HIGHLIGHTS", "BLOCKERS", "SPEAKER_NOTES"];
const layoutTypes: SlideLayoutType[] = ["TITLE_AND_BULLETS", "TITLE_BODY_NOTES", "SECTION_SUMMARY", "TITLE_ONLY"];

export function AddSlideMenu({
  disabled,
  onAdd,
}: {
  disabled?: boolean;
  onAdd: (payload: AddSlideRequest) => void;
}) {
  const [slideType, setSlideType] = useState<SlideType>("CUSTOM");
  const [layoutType, setLayoutType] = useState<SlideLayoutType>("TITLE_AND_BULLETS");

  return (
    <div className="rounded-3xl border border-line bg-white p-4">
      <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Add slide</p>
      <div className="mt-3 grid gap-3">
        <select
          className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          onChange={(event) => setSlideType(event.target.value as SlideType)}
          value={slideType}
        >
          {slideTypes.map((type) => (
            <option key={type} value={type}>
              {type.replaceAll("_", " ")}
            </option>
          ))}
        </select>
        <select
          className="rounded-2xl border border-line bg-white px-3 py-2 text-sm text-ink"
          onChange={(event) => setLayoutType(event.target.value as SlideLayoutType)}
          value={layoutType}
        >
          {layoutTypes.map((type) => (
            <option key={type} value={type}>
              {type.replaceAll("_", " ")}
            </option>
          ))}
        </select>
        <Button
          disabled={disabled}
          onClick={() => onAdd({ slideType, layoutType, title: null, sectionLabel: null })}
          type="button"
          variant="secondary"
        >
          Add slide
        </Button>
      </div>
    </div>
  );
}
