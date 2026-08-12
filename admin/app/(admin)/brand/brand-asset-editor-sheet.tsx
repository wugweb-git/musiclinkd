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
import { brandAssetsRepo } from "@/services/repositories";
import type { BrandAssetsDoc, WithId } from "@/types/firestore";

interface BrandAssetEditorSheetProps {
  asset: WithId<BrandAssetsDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const URL_FIELDS: Array<{ key: keyof Pick<BrandAssetsDoc, "logoUrl" | "iconUrl" | "thumbnailUrl" | "heroBannerUrl" | "darkVariantUrl" | "lightVariantUrl">; label: string; placeholder: string }> = [
  { key: "logoUrl", label: "Logo URL", placeholder: "Primary wordmark/logo" },
  { key: "iconUrl", label: "Icon URL", placeholder: "Square app-icon-style mark" },
  { key: "thumbnailUrl", label: "Thumbnail URL", placeholder: "Small preview image" },
  { key: "heroBannerUrl", label: "Hero Banner URL", placeholder: "Wide profile-header banner" },
  { key: "darkVariantUrl", label: "Dark Variant URL", placeholder: "Logo for dark backgrounds" },
  { key: "lightVariantUrl", label: "Light Variant URL", placeholder: "Logo for light backgrounds" },
];

export function BrandAssetEditorSheet({ asset, open, onOpenChange, onSaved }: BrandAssetEditorSheetProps) {
  const [ownerUid, setOwnerUid] = useState("");
  const [urls, setUrls] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (asset) {
      setOwnerUid(asset.data.ownerUid);
      setUrls({
        logoUrl: asset.data.logoUrl ?? "",
        iconUrl: asset.data.iconUrl ?? "",
        thumbnailUrl: asset.data.thumbnailUrl ?? "",
        heroBannerUrl: asset.data.heroBannerUrl ?? "",
        darkVariantUrl: asset.data.darkVariantUrl ?? "",
        lightVariantUrl: asset.data.lightVariantUrl ?? "",
      });
    } else {
      setOwnerUid("");
      setUrls({});
    }
  }, [asset, open]);

  async function handleSave() {
    if (!ownerUid) return;
    setSaving(true);
    try {
      const id = asset?.id ?? crypto.randomUUID();
      const now = brandAssetsRepo.serverTimestamp();
      const nonEmptyUrls = Object.fromEntries(Object.entries(urls).filter(([, v]) => v.trim() !== ""));
      await brandAssetsRepo.create(id, {
        ownerUid,
        ...nonEmptyUrls,
        updatedAt: now as never,
      });
      toast.success(asset ? "Brand assets updated" : "Brand assets created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save brand assets");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{asset ? "Edit brand assets" : "New brand asset set"}</SheetTitle>
          <SheetDescription>Logo/icon/banner URLs for one business profile — public-read once saved.</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="brand-owner-uid">Owner UID</Label>
            <Input
              id="brand-owner-uid"
              value={ownerUid}
              onChange={(e) => setOwnerUid(e.target.value)}
              placeholder="The business/user doc ID this brand kit belongs to"
              disabled={!!asset}
            />
          </div>
          {URL_FIELDS.map((f) => (
            <div key={f.key} className="space-y-1.5">
              <Label htmlFor={`brand-${f.key}`}>{f.label}</Label>
              <Input
                id={`brand-${f.key}`}
                value={urls[f.key] ?? ""}
                onChange={(e) => setUrls((u) => ({ ...u, [f.key]: e.target.value }))}
                placeholder={f.placeholder}
              />
            </div>
          ))}
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !ownerUid}>
            {saving ? "Saving…" : asset ? "Save changes" : "Create"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
