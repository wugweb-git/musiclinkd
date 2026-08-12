"use client";

import { useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
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
import { Textarea } from "@/components/ui/textarea";
import { useAllFlowScreens } from "@/hooks/use-all-flow-screens";
import { widgetsRepo } from "@/services/repositories";
import type { WidgetDoc, WithId } from "@/types/firestore";

interface WidgetEditorSheetProps {
  widget: WithId<WidgetDoc> | null;
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

export function WidgetEditorSheet({ widget, open, onOpenChange, onSaved, nextOrder }: WidgetEditorSheetProps) {
  const [widgetId, setWidgetId] = useState("");
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [purpose, setPurpose] = useState("");
  const [contentComposition, setContentComposition] = useState("");
  const [codeMapping, setCodeMapping] = useState("");
  const [order, setOrder] = useState(0);
  const [visibilityKnown, setVisibilityKnown] = useState(false);
  const [isVisible, setIsVisible] = useState(true);
  const [notes, setNotes] = useState("");
  const [relatedScreens, setRelatedScreens] = useState<number[]>([]);
  const [screenFilter, setScreenFilter] = useState("");
  const [saving, setSaving] = useState(false);
  const { options: screenOptions, loading: screensLoading } = useAllFlowScreens();

  useEffect(() => {
    if (widget) {
      setWidgetId(widget.id);
      setName(widget.data.name);
      setCategory(widget.data.category);
      setPurpose(widget.data.purpose);
      setContentComposition(widget.data.contentComposition);
      setCodeMapping(widget.data.codeMapping);
      setOrder(widget.data.order);
      setVisibilityKnown(widget.data.isVisible !== undefined);
      setIsVisible(widget.data.isVisible ?? true);
      setNotes(widget.data.notes ?? "");
      setRelatedScreens(widget.data.relatedScreens ?? []);
    } else {
      setWidgetId("");
      setName("");
      setCategory("");
      setPurpose("");
      setContentComposition("");
      setCodeMapping("");
      setOrder(nextOrder);
      setVisibilityKnown(false);
      setIsVisible(true);
      setNotes("");
      setRelatedScreens([]);
    }
  }, [widget, open, nextOrder]);

  const filteredScreenOptions = useMemo(() => {
    const q = screenFilter.trim().toLowerCase();
    if (!q) return screenOptions;
    return screenOptions.filter((o) => o.label.toLowerCase().includes(q) || o.flowTitle.toLowerCase().includes(q) || String(o.screenNumber) === q);
  }, [screenOptions, screenFilter]);

  function toggleScreen(screenNumber: number) {
    setRelatedScreens((prev) => (prev.includes(screenNumber) ? prev.filter((n) => n !== screenNumber) : [...prev, screenNumber].sort((a, b) => a - b)));
  }

  async function handleSave() {
    if (!name || !category) return;
    setSaving(true);
    try {
      const id = widgetId || slugify(name) || crypto.randomUUID();
      const now = widgetsRepo.serverTimestamp();
      // Firestore rejects `undefined` field values outright — omit isVisible/notes
      // entirely rather than set them to undefined when left blank.
      await widgetsRepo.create(id, {
        key: id,
        order,
        name,
        category,
        purpose,
        contentComposition,
        codeMapping,
        ...(visibilityKnown ? { isVisible } : {}),
        ...(notes ? { notes } : {}),
        ...(relatedScreens.length ? { relatedScreens } : {}),
        createdAt: (widget?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      });
      toast.success(widget ? "Widget updated" : "Widget created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save widget");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{widget ? `Edit ${widget.data.name}` : "New widget"}</SheetTitle>
          <SheetDescription>{widget ? `Key: ${widgetId}` : "Key is auto-generated from the name."}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="widget-name">Name</Label>
              <Input id="widget-name" value={name} onChange={(e) => setName(e.target.value)} placeholder="ArtistCard" />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="widget-category">Category</Label>
              <Input
                id="widget-category"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                placeholder="Discovery & Collaboration"
              />
            </div>
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="widget-purpose">Purpose / use case</Label>
            <Textarea id="widget-purpose" value={purpose} onChange={(e) => setPurpose(e.target.value)} rows={2} />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="widget-composition">Content / composition</Label>
            <Textarea
              id="widget-composition"
              value={contentComposition}
              onChange={(e) => setContentComposition(e.target.value)}
              rows={2}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="widget-code-mapping">Code implementation mapping</Label>
            <Input id="widget-code-mapping" value={codeMapping} onChange={(e) => setCodeMapping(e.target.value)} placeholder="UnifiedArtistCard" />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="widget-order">Order</Label>
            <Input id="widget-order" type="number" value={order} onChange={(e) => setOrder(Number(e.target.value))} />
          </div>

          <div className="space-y-2 rounded-md border p-3">
            <div className="flex items-center gap-2">
              <Checkbox id="widget-visibility-known" checked={visibilityKnown} onCheckedChange={(v) => setVisibilityKnown(v === true)} />
              <Label htmlFor="widget-visibility-known" className="font-normal">
                Visibility confirmed (uncheck if you haven&apos;t verified whether this widget&apos;s call site is actually live)
              </Label>
            </div>
            {visibilityKnown ? (
              <div className="flex items-center gap-2 pl-6">
                <Checkbox id="widget-is-visible" checked={isVisible} onCheckedChange={(v) => setIsVisible(v === true)} />
                <Label htmlFor="widget-is-visible" className="font-normal">
                  Currently shown in the app&apos;s UI
                </Label>
              </div>
            ) : null}
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="widget-notes">Notes</Label>
            <Textarea id="widget-notes" value={notes} onChange={(e) => setNotes(e.target.value)} rows={3} />
          </div>

          <div className="space-y-2 rounded-md border p-3">
            <div className="flex items-center justify-between gap-2">
              <Label>Related screens</Label>
              {relatedScreens.length > 0 ? <Badge variant="outline">{relatedScreens.length} selected</Badge> : null}
            </div>
            <p className="text-xs text-muted-foreground">Which screens actually render this widget — many-to-many, unconfirmed until checked against source.</p>
            <Input
              placeholder="Filter by screen, flow, or number..."
              value={screenFilter}
              onChange={(e) => setScreenFilter(e.target.value)}
            />
            <div className="max-h-48 space-y-1 overflow-y-auto rounded border p-2">
              {screensLoading ? (
                <p className="text-xs text-muted-foreground">Loading screens…</p>
              ) : filteredScreenOptions.length === 0 ? (
                <p className="text-xs text-muted-foreground">No screens match.</p>
              ) : (
                filteredScreenOptions.map((opt) => (
                  <label key={opt.screenNumber} className="flex cursor-pointer items-center gap-2 rounded px-1 py-1 text-sm hover:bg-accent">
                    <Checkbox checked={relatedScreens.includes(opt.screenNumber)} onCheckedChange={() => toggleScreen(opt.screenNumber)} />
                    <span className="w-8 shrink-0 font-mono text-xs text-muted-foreground">#{opt.screenNumber}</span>
                    <span className="truncate">{opt.label}</span>
                    <span className="ml-auto shrink-0 truncate text-xs text-muted-foreground">{opt.flowTitle}</span>
                  </label>
                ))
              )}
            </div>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !name || !category}>
            {saving ? "Saving…" : widget ? "Save changes" : "Create widget"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
