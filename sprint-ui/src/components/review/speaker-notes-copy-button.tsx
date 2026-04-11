import { Button } from "@/components/ui/button";

export function SpeakerNotesCopyButton({
  disabled,
  isPending,
  onClick,
}: {
  disabled?: boolean;
  isPending?: boolean;
  onClick: () => void;
}) {
  return (
    <Button disabled={disabled || isPending} type="button" variant="secondary" onClick={onClick}>
      {isPending ? "Copying speaker notes..." : "Copy speaker notes"}
    </Button>
  );
}
