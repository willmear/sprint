const handles = [
  { key: 'nw', className: '-left-1.5 -top-1.5 cursor-nwse-resize' },
  { key: 'ne', className: '-right-1.5 -top-1.5 cursor-nesw-resize' },
  { key: 'sw', className: '-bottom-1.5 -left-1.5 cursor-nesw-resize' },
  { key: 'se', className: '-bottom-1.5 -right-1.5 cursor-nwse-resize' },
] as const;

export type ResizeHandle = (typeof handles)[number]["key"];

export function SelectionOverlay({
  onResizeStart,
}: {
  onResizeStart: (handle: ResizeHandle, event: React.PointerEvent<HTMLButtonElement>) => void;
}) {
  return (
    <div className="pointer-events-none absolute inset-0 rounded-[4px] border border-blue-500 shadow-[0_0_0_1px_rgba(255,255,255,0.9)]">
      <div className="absolute -top-7 left-0 rounded-md border border-blue-300 bg-white px-2 py-1 text-[10px] font-semibold uppercase tracking-[0.14em] text-blue-700 shadow-sm">
        Text box
      </div>
      {handles.map((handle) => (
        <button
          key={handle.key}
          className={`pointer-events-auto absolute h-3 w-3 rounded-full border border-white bg-blue-500 shadow ${handle.className}`}
          onPointerDown={(event) => onResizeStart(handle.key, event)}
          type="button"
        />
      ))}
    </div>
  );
}
