"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Textarea } from "@/components/ui/textarea";
import { flowScreensRepo } from "@/services/repositories/flow-screens-repository";
import type { FlowScreenDoc, WithId } from "@/types/firestore";

interface ScreenEditorSheetProps {
  flowId: string;
  screen: WithId<FlowScreenDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
  nextOrder: number;
}

function slugify(label: string) {
  return label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

function csv(value: unknown): string {
  return Array.isArray(value) ? value.join(", ") : "";
}

function fromCsv(text: string): string[] {
  return text
    .split(",")
    .map((v) => v.trim())
    .filter(Boolean);
}

export function ScreenEditorSheet({ flowId, screen, open, onOpenChange, onSaved, nextOrder }: ScreenEditorSheetProps) {
  const repo = flowScreensRepo(flowId);
  const [screenId, setScreenId] = useState("");
  const [title, setTitle] = useState("");
  const [order, setOrder] = useState(0);
  const [statesText, setStatesText] = useState("");
  const [collectionsText, setCollectionsText] = useState("");
  const [notes, setNotes] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (screen) {
      setScreenId(screen.id);
      setTitle(screen.data.title);
      setOrder(screen.data.order);
      setStatesText(csv(screen.data.states));
      setCollectionsText(csv(screen.data.collectionsTouched));
      setNotes(screen.data.notes ?? "");
    } else {
      setScreenId("");
      setTitle("");
      setOrder(nextOrder);
      setStatesText("");
      setCollectionsText("");
      setNotes("");
    }
  }, [screen, open, nextOrder]);

  async function handleSave() {
    if (!title) return;
    setSaving(true);
    try {
      const id = screenId || slugify(title) || crypto.randomUUID();
      const now = repo.serverTimestamp();
      await repo.create(id, {
        key: id,
        order,
        title,
        states: fromCsv(statesText),
        collectionsTouched: fromCsv(collectionsText),
        notes,
        createdAt: (screen?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      });
      toast.success(screen ? "Screen updated" : "Screen created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save screen");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{screen ? `Edit ${screen.data.title}` : "New screen"}</SheetTitle>
          <SheetDescription>{screen ? `Key: ${screenId}` : "Key is auto-generated from the title."}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="screen-title">Title</Label>
              <Input id="screen-title" value={title} onChange={(e) => setTitle(e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="screen-order">Order</Label>
              <Input id="screen-order" type="number" value={order} onChange={(e) => setOrder(Number(e.target.value))} />
            </div>
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="screen-states">States (comma-separated)</Label>
            <Input
              id="screen-states"
              value={statesText}
              onChange={(e) => setStatesText(e.target.value)}
              placeholder="loading, loaded, error, empty — or per-role variants"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="screen-collections">Firestore collections touched (comma-separated)</Label>
            <Input
              id="screen-collections"
              value={collectionsText}
              onChange={(e) => setCollectionsText(e.target.value)}
              placeholder="users, portfolio_projects"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="screen-notes">Notes</Label>
            <Textarea id="screen-notes" value={notes} onChange={(e) => setNotes(e.target.value)} rows={3} />
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !title}>
            {saving ? "Saving…" : screen ? "Save changes" : "Create screen"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
