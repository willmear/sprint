import { Button } from "@/components/ui/button";

export function ElementActions({
  canAct,
  onDuplicate,
  onDelete,
}: {
  canAct: boolean;
  onDuplicate: () => void;
  onDelete: () => void;
}) {
  return (
    <div className="flex items-center gap-2">
      <Button disabled={!canAct} onClick={onDuplicate} type="button" variant="secondary">
        Duplicate text box
      </Button>
      <Button disabled={!canAct} onClick={onDelete} type="button" variant="secondary">
        Delete text box
      </Button>
    </div>
  );
}
