import { cn } from "@/lib/utils/cn";
import { resolvePresentationTheme } from "@/lib/presentation-themes";
import { resolveSlideChrome } from "@/lib/presentation-preview";
import type { PresentationSlide } from "@/types/presentation";

export function SlideThumbnail({
  slide,
  selected,
  onSelect,
  draggable,
  onDragStart,
  onDragOver,
  onDragEnd,
  onDrop,
}: {
  slide: PresentationSlide;
  selected: boolean;
  onSelect: () => void;
  draggable?: boolean;
  onDragStart?: () => void;
  onDragOver?: () => void;
  onDragEnd?: () => void;
  onDrop?: () => void;
}) {
  const previewBody = slide.bodyText || slide.bulletPoints.slice(0, 3).join(" • ") || "Click to edit this slide";
  const chrome = resolveSlideChrome(resolvePresentationTheme(), slide);

  return (
    <button
      className={cn(
        "group flex w-full items-start gap-3 rounded-lg border px-2.5 py-2 text-left transition",
        selected ? "border-blue-500 bg-blue-50 shadow-sm" : "border-transparent bg-transparent hover:border-slate-200 hover:bg-white"
      )}
      draggable={draggable}
      onClick={onSelect}
      onDragEnd={onDragEnd}
      onDragOver={(event) => {
        event.preventDefault();
        onDragOver?.();
      }}
      onDragStart={onDragStart}
      onDrop={(event) => {
        event.preventDefault();
        onDrop?.();
      }}
      type="button"
    >
      <span className={cn("mt-2 w-5 text-center text-[11px] font-medium", selected ? "text-blue-700" : "text-slate-400")}>{slide.slideOrder + 1}</span>
      <div className="min-w-0 flex-1">
        <div className={cn("relative aspect-[16/9] overflow-hidden rounded-md border shadow-sm", selected ? "border-blue-400" : "border-slate-200")} style={{ backgroundColor: chrome.backgroundColor }}>
          {chrome.fullBleedHeaderAccent ? <div className="absolute left-0 top-0 h-[8%] w-full" style={{ backgroundColor: chrome.accentColor }} /> : null}
          {chrome.leftAccentRail ? <div className="absolute left-0 top-0 h-full w-[3%]" style={{ backgroundColor: chrome.accentColor }} /> : null}
          {chrome.fullBleedBottomAccent ? <div className="absolute bottom-0 left-0 h-[7%] w-full" style={{ backgroundColor: chrome.accentColor }} /> : null}
          <div className="relative flex h-full w-full flex-col gap-1.5 p-2.5">
            <div className="line-clamp-2 text-[10px] font-semibold leading-tight" style={{ color: chrome.titleColor }}>{slide.title}</div>
            <div className="line-clamp-4 text-[8px] leading-snug" style={{ color: chrome.subtitleColor }}>{previewBody}</div>
          </div>
        </div>
        <div className="mt-1.5 line-clamp-1 text-[11px] font-medium text-slate-700">{slide.title}</div>
      </div>
    </button>
  );
}
