"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Checkbox } from "@/components/ui/checkbox";
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
import { useAuth } from "@/hooks/use-auth";
import { taxonomyCategoriesRepo } from "@/services/repositories";
import type { TaxonomyCategoryDoc } from "@/types/firestore";

interface CategoryEditorSheetProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

function slugify(label: string) {
  return label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

/** Categories are always created new here — editing metadata later is a follow-up, not needed yet since only the "genres" seed category exists at launch. */
export function CategoryEditorSheet({ open, onOpenChange, onSaved }: CategoryEditorSheetProps) {
  const { user } = useAuth();
  const [label, setLabel] = useState("");
  const [description, setDescription] = useState("");
  const [isActive, setIsActive] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (open) {
      setLabel("");
      setDescription("");
      setIsActive(true);
    }
  }, [open]);

  const key = slugify(label);

  async function handleSave() {
    if (!key || !user) return;
    setSaving(true);
    try {
      const now = taxonomyCategoriesRepo.serverTimestamp();
      const doc: TaxonomyCategoryDoc = {
        key,
        label,
        description,
        isActive,
        createdAt: now as never,
        updatedAt: now as never,
        updatedBy: user.uid,
      };
      await taxonomyCategoriesRepo.create(key, doc);
      toast.success("Category created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to create category — a category with that key may already exist.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>New category</SheetTitle>
          <SheetDescription>
            Creates a controlled reference list (e.g. &quot;Genres&quot;) that editor sheets elsewhere in
            the admin panel can source options from. Add its options on the next screen once created.
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="category-label">Label</Label>
            <Input
              id="category-label"
              value={label}
              onChange={(e) => setLabel(e.target.value)}
              placeholder="Genres"
            />
            {key ? <p className="text-xs text-muted-foreground">Key: {key}</p> : null}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="category-description">Description</Label>
            <Textarea
              id="category-description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
            />
          </div>
          <label className="flex items-center gap-2 text-sm">
            <Checkbox checked={isActive} onCheckedChange={(c) => setIsActive(c === true)} />
            Active
          </label>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !key}>
            {saving ? "Creating…" : "Create category"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
