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
import { CappedCheckboxList } from "@/components/shared/capped-checkbox-list";
import { useTaxonomyOptions } from "@/hooks/use-taxonomy-options";
import { studiosRepo } from "@/services/repositories";
import type { StudioDoc, WithId } from "@/types/firestore";

interface StudioEditorSheetProps {
  studio: WithId<StudioDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

function emptyForm(): StudioDoc {
  return {
    ownerUid: "",
    name: "",
    location: "",
    rating: 0,
    reviewCount: 0,
    hourlyRate: 0,
    verifiedGold: false,
    amenities: [],
    consoleType: "",
    imageUrl: "",
  };
}

export function StudioEditorSheet({ studio, open, onOpenChange, onSaved }: StudioEditorSheetProps) {
  const [id, setId] = useState("");
  const [form, setForm] = useState<StudioDoc>(emptyForm());
  const [amenitiesText, setAmenitiesText] = useState("");
  const [saving, setSaving] = useState(false);

  const amenityOptions = useTaxonomyOptions("studio_amenities");

  useEffect(() => {
    if (studio) {
      setId(studio.id);
      const amenities = Array.isArray(studio.data.amenities) ? studio.data.amenities : [];
      setForm({ ...studio.data, amenities });
      setAmenitiesText(amenities.join(", "));
    } else {
      setId(crypto.randomUUID());
      setForm(emptyForm());
      setAmenitiesText("");
    }
  }, [studio, open]);

  function toggleAmenity(value: string) {
    setForm((f) => {
      const has = f.amenities.includes(value);
      return { ...f, amenities: has ? f.amenities.filter((a) => a !== value) : [...f.amenities, value] };
    });
  }

  async function handleSave() {
    if (!form.name || !form.location) return;
    setSaving(true);
    try {
      const amenities = amenityOptions
        ? form.amenities
        : amenitiesText
            .split(",")
            .map((a) => a.trim())
            .filter(Boolean);
      await studiosRepo.create(id, { ...form, amenities });
      toast.success(studio ? "Studio updated" : "Studio created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save studio");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{studio ? `Edit ${studio.data.name}` : "New studio"}</SheetTitle>
          <SheetDescription>
            {studio
              ? `Doc ID: ${id}`
              : "Owner UID can be left blank for a partner studio that hasn't signed up yet — fill it in once they claim the listing. Leaving it blank means no user account can self-manage this listing until then."}
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="studio-name">Name</Label>
              <Input
                id="studio-name"
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="studio-location">Location</Label>
              <Input
                id="studio-location"
                value={form.location}
                onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="studio-rate">Hourly rate (₹/hr, 0 = unset)</Label>
              <Input
                id="studio-rate"
                type="number"
                min={0}
                value={form.hourlyRate}
                onChange={(e) => setForm((f) => ({ ...f, hourlyRate: e.target.valueAsNumber || 0 }))}
                placeholder="2500"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="studio-console">Console type</Label>
              <Input
                id="studio-console"
                value={form.consoleType}
                onChange={(e) => setForm((f) => ({ ...f, consoleType: e.target.value }))}
                placeholder="SSL Duality, Neve, ..."
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="studio-rating">Rating</Label>
              <Input
                id="studio-rating"
                type="number"
                min={0}
                max={5}
                step={0.1}
                value={form.rating}
                onChange={(e) => setForm((f) => ({ ...f, rating: Number(e.target.value) }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="studio-review-count">Review count</Label>
              <Input
                id="studio-review-count"
                type="number"
                min={0}
                value={form.reviewCount}
                onChange={(e) => setForm((f) => ({ ...f, reviewCount: Number(e.target.value) }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="studio-owner">Owner UID</Label>
              <Input
                id="studio-owner"
                value={form.ownerUid}
                onChange={(e) => setForm((f) => ({ ...f, ownerUid: e.target.value }))}
                placeholder="optional"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="studio-image">Image URL</Label>
            <Input
              id="studio-image"
              value={form.imageUrl}
              onChange={(e) => setForm((f) => ({ ...f, imageUrl: e.target.value }))}
            />
          </div>

          {amenityOptions ? (
            <CappedCheckboxList
              label="Amenities"
              options={amenityOptions}
              selected={form.amenities}
              onToggle={toggleAmenity}
            />
          ) : (
            <div className="space-y-1.5">
              <Label htmlFor="studio-amenities">Amenities (comma-separated)</Label>
              <Input
                id="studio-amenities"
                value={amenitiesText}
                onChange={(e) => setAmenitiesText(e.target.value)}
                placeholder="Vocal Booth, Live Room, Dolby Atmos"
              />
              <p className="text-xs text-muted-foreground">
                No &quot;studio_amenities&quot; master-data category yet — using freeform text.
              </p>
            </div>
          )}

          <label className="flex items-center gap-2 pt-1 text-sm">
            <Checkbox
              checked={form.verifiedGold}
              onCheckedChange={(c) => setForm((f) => ({ ...f, verifiedGold: c === true }))}
            />
            Verified (gold badge)
          </label>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !form.name || !form.location}>
            {saving ? "Saving…" : studio ? "Save changes" : "Create studio"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
