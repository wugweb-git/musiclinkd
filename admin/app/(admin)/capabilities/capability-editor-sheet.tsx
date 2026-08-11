"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { capabilitiesRepo } from "@/services/repositories";
import type { CapabilityMatrixDoc, CrmAccessLevel, WithId } from "@/types/firestore";

interface CapabilityEditorSheetProps {
  capability: WithId<CapabilityMatrixDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const CRM_LEVELS: CrmAccessLevel[] = ["none", "partial", "full"];

function slugify(label: string) {
  return label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

export function CapabilityEditorSheet({ capability, open, onOpenChange, onSaved }: CapabilityEditorSheetProps) {
  const [roleKey, setRoleKey] = useState("");
  const [label, setLabel] = useState("");
  const [canCreateProfile, setCanCreateProfile] = useState(true);
  const [canPostOpportunities, setCanPostOpportunities] = useState(false);
  const [canSellEquipment, setCanSellEquipment] = useState(false);
  const [canCreateEvents, setCanCreateEvents] = useState(false);
  const [crmAccess, setCrmAccess] = useState<CrmAccessLevel>("none");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (capability) {
      setRoleKey(capability.id);
      setLabel(capability.data.label);
      setCanCreateProfile(capability.data.canCreateProfile);
      setCanPostOpportunities(capability.data.canPostOpportunities);
      setCanSellEquipment(capability.data.canSellEquipment);
      setCanCreateEvents(capability.data.canCreateEvents);
      setCrmAccess(capability.data.crmAccess);
    } else {
      setRoleKey("");
      setLabel("");
      setCanCreateProfile(true);
      setCanPostOpportunities(false);
      setCanSellEquipment(false);
      setCanCreateEvents(false);
      setCrmAccess("none");
    }
  }, [capability, open]);

  async function handleSave() {
    if (!label) return;
    setSaving(true);
    try {
      const id = roleKey || slugify(label) || crypto.randomUUID();
      const now = capabilitiesRepo.serverTimestamp();
      await capabilitiesRepo.create(id, {
        roleKey: id,
        label,
        canCreateProfile,
        canPostOpportunities,
        canSellEquipment,
        canCreateEvents,
        crmAccess,
        updatedBy: "admin",
        createdAt: (capability?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      });
      toast.success(capability ? "Capability updated" : "Capability created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save capability");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{capability ? `Edit ${capability.data.label}` : "New role capability"}</SheetTitle>
          <SheetDescription>{capability ? `Key: ${roleKey}` : "Key is auto-generated from the label."}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="cap-label">Label</Label>
            <Input id="cap-label" value={label} onChange={(e) => setLabel(e.target.value)} placeholder="Talent" />
          </div>
          <div className="flex flex-wrap gap-4">
            <label className="flex items-center gap-2 text-sm">
              <Checkbox checked={canCreateProfile} onCheckedChange={(c) => setCanCreateProfile(c === true)} />
              Can Create Profile
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Checkbox checked={canPostOpportunities} onCheckedChange={(c) => setCanPostOpportunities(c === true)} />
              Can Post Opportunities
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Checkbox checked={canSellEquipment} onCheckedChange={(c) => setCanSellEquipment(c === true)} />
              Can Sell Equipment
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Checkbox checked={canCreateEvents} onCheckedChange={(c) => setCanCreateEvents(c === true)} />
              Can Create Events
            </label>
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="cap-crm">CRM Access</Label>
            <Select value={crmAccess} onValueChange={(v) => setCrmAccess((v as CrmAccessLevel) ?? "none")}>
              <SelectTrigger id="cap-crm" className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {CRM_LEVELS.map((l) => (
                  <SelectItem key={l} value={l}>
                    {l}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !label}>
            {saving ? "Saving…" : capability ? "Save changes" : "Create capability"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
