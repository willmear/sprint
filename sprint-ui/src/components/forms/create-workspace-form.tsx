"use client";

import { FormEvent, useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useCreateWorkspace } from "@/lib/hooks/use-workspaces";

export function CreateWorkspaceForm() {
  const createWorkspace = useCreateWorkspace();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await createWorkspace.mutateAsync({
      name,
      description: description || undefined,
    });
    setName("");
    setDescription("");
  }

  return (
    <form className="space-y-4" onSubmit={handleSubmit}>
      <div className="space-y-2">
        <label className="text-sm font-medium text-stone-700" htmlFor="workspace-name">
          Workspace name
        </label>
        <Input
          id="workspace-name"
          placeholder="Customer Platform"
          required
          value={name}
          onChange={(event) => setName(event.target.value)}
        />
      </div>
      <div className="space-y-2">
        <label className="text-sm font-medium text-stone-700" htmlFor="workspace-description">
          Description
        </label>
        <Textarea
          id="workspace-description"
          placeholder="Internal dashboard and workflow automation team"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />
      </div>
      <Button disabled={createWorkspace.isPending} type="submit">
        {createWorkspace.isPending ? "Creating..." : "Create workspace"}
      </Button>
      {createWorkspace.error ? <p className="text-sm text-rose-600">{createWorkspace.error.message}</p> : null}
    </form>
  );
}
