"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
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
import { taxonomyOptionsRepo } from "@/services/repositories/taxonomy-options-repository";
import type { TaxonomyOptionDoc, WithId } from "@/types/firestore";

interface OptionEditorSheetProps {
  categoryId: string;
  option: WithId<TaxonomyOptionDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
  nextSortOrder: number;
}

function slugify(label: string) {
  return label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

export function OptionEditorSheet({
  categoryId,
  option,
  open,
  onOpenChange,
  onSaved,
  nextSortOrder,
}: OptionEditorSheetProps) {
  const repo = taxonomyOptionsRepo(categoryId);
  const [optionId, setOptionId] = useState("");
  const [label, setLabel] = useState("");
  const [value, setValue] = useState("");
  const [sortOrder, setSortOrder] = useState(0);
  const [isActive, setIsActive] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (option) {
      setOptionId(option.id);
      setLabel(option.data.label);
      setValue(option.data.value);
      setSortOrder(option.data.sortOrder);
      setIsActive(option.data.isActive);
    } else {
      setOptionId("");
      setLabel("");
      setValue("");
      setSortOrder(nextSortOrder);
      setIsActive(true);
    }
  }, [option, open, nextSortOrder]);

  async function handleSave() {
    if (!label) return;
    setSaving(true);
    try {
      const id = optionId || slugify(label) || crypto.randomUUID();
      const now = repo.serverTimestamp();
      const doc: TaxonomyOptionDoc = {
        label,
        value: value || slugify(label),
        sortOrder,
        isActive,
        createdAt: (option?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      };
      await repo.create(id, doc);
      toast.success(option ? "Option updated" : "Option created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save option");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{option ? `Edit ${option.data.label}` : "New option"}</SheetTitle>
          <SheetDescription>Options in &quot;{categoryId}&quot;.</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="option-label">Label</Label>
            <Input
              id="option-label"
              value={label}
              onChange={(e) => setLabel(e.target.value)}
              placeholder="Bollywood / Filmi"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="option-value">Value (stable, machine-readable)</Label>
            <Input
              id="option-value"
              value={value}
              onChange={(e) => setValue(e.target.value)}
              placeholder={slugify(label) || "auto-generated from label"}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="option-sort">Sort order</Label>
            <Input
              id="option-sort"
              type="number"
              value={sortOrder}
              onChange={(e) => setSortOrder(Number(e.target.value))}
            />
          </div>
          <label className="flex items-center gap-2 text-sm">
            <Checkbox checked={isActive} onCheckedChange={(c) => setIsActive(c === true)} />
            Active
          </label>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !label}>
            {saving ? "Saving…" : option ? "Save changes" : "Create option"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
