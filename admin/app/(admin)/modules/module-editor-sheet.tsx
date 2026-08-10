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
import { useAuth } from "@/hooks/use-auth";
import { modulesRepo } from "@/services/repositories";
import type { ModuleRegistryDoc, WithId } from "@/types/firestore";

interface ModuleEditorSheetProps {
  module: WithId<ModuleRegistryDoc> | null;
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

export function ModuleEditorSheet({ module, open, onOpenChange, onSaved }: ModuleEditorSheetProps) {
  const { user } = useAuth();
  const [key, setKey] = useState("");
  const [label, setLabel] = useState("");
  const [description, setDescription] = useState("");
  const [navModuleKey, setNavModuleKey] = useState("");
  const [remoteConfigKey, setRemoteConfigKey] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (module) {
      setKey(module.id);
      setLabel(module.data.label);
      setDescription(module.data.description);
      setNavModuleKey(module.data.navModuleKey ?? "");
      setRemoteConfigKey(module.data.remoteConfigKey ?? "");
    } else {
      setKey("");
      setLabel("");
      setDescription("");
      setNavModuleKey("");
      setRemoteConfigKey("");
    }
  }, [module, open]);

  async function handleSave() {
    if (!user) return;
    const id = module?.id || slugify(label);
    if (!id || !label) return;
    setSaving(true);
    try {
      const now = modulesRepo.serverTimestamp();
      if (module) {
        // Metadata only — `enabled` is intentionally omitted here; rules
        // block clients from changing it outside the toggleModule Cloud
        // Function, and this update wouldn't include it even if allowed.
        await modulesRepo.update(module.id, {
          label,
          description,
          navModuleKey: navModuleKey || null,
          remoteConfigKey: remoteConfigKey || null,
          updatedBy: user.uid,
          updatedAt: now as never,
        });
        toast.success("Module updated");
      } else {
        const doc: ModuleRegistryDoc = {
          label,
          description,
          enabled: false,
          navModuleKey: navModuleKey || null,
          remoteConfigKey: remoteConfigKey || null,
          updatedBy: user.uid,
          updatedAt: now as never,
          createdAt: now as never,
        };
        await modulesRepo.create(id, doc);
        toast.success("Module registered (starts disabled — enable it from the list)");
      }
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save module");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{module ? `Edit ${module.data.label}` : "New module"}</SheetTitle>
          <SheetDescription>
            {module
              ? "Metadata only — use the Enable/Disable button on the list to change its live state."
              : "Registers a new module, always starting disabled. Enable it afterward from the list."}
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="module-label">Label</Label>
            <Input id="module-label" value={label} onChange={(e) => setLabel(e.target.value)} placeholder="Marketplace" />
            {!module && key ? <p className="text-xs text-muted-foreground">Key: {key}</p> : null}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="module-description">Description</Label>
            <Textarea
              id="module-description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="module-nav">Admin nav item key (optional)</Label>
            <Input
              id="module-nav"
              value={navModuleKey}
              onChange={(e) => setNavModuleKey(e.target.value)}
              placeholder="marketplace"
            />
            <p className="text-xs text-muted-foreground">
              Must match a real key in config/constants.ts MODULE_PERMISSIONS to gate an actual admin
              route. Leave blank for a Remote-Config-only toggle with no admin page.
            </p>
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="module-rc">Remote Config key (optional)</Label>
            <Input
              id="module-rc"
              value={remoteConfigKey}
              onChange={(e) => setRemoteConfigKey(e.target.value)}
              placeholder="module_marketplace_enabled"
            />
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !label}>
            {saving ? "Saving…" : module ? "Save changes" : "Register module"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
