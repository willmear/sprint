import { Button } from "@/components/ui/button";

export function AddTextBoxButton({
  disabled,
  onClick,
}: {
  disabled?: boolean;
  onClick: () => void;
}) {
  return (
    <Button disabled={disabled} onClick={onClick} type="button" variant="secondary">
      Add text box
    </Button>
  );
}
