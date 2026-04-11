export function FontSizeInput({
  disabled,
  value,
  onChange,
}: {
  disabled: boolean;
  value: number | "";
  onChange: (value: number) => void;
}) {
  return (
    <label className="flex items-center gap-2 text-sm text-stone-600">
      <span className="text-xs uppercase tracking-[0.14em] text-stone-500">Size</span>
      <input
        className="w-20 rounded-2xl border border-line bg-cloud px-3 py-2 text-sm text-ink outline-none transition focus:border-blue-400 disabled:cursor-not-allowed disabled:opacity-60"
        disabled={disabled}
        max={144}
        min={8}
        onChange={(event) => onChange(Number(event.target.value))}
        type="number"
        value={value}
      />
    </label>
  );
}
