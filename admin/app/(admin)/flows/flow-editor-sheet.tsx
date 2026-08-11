"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
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
import { Textarea } from "@/components/ui/textarea";
import { flowsRepo } from "@/services/repositories";
import type { FlowDoc, FlowStatus, WithId } from "@/types/firestore";

interface FlowEditorSheetProps {
  flow: WithId<FlowDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
  nextOrder: number;
}

const STATUSES: FlowStatus[] = ["documented", "partial", "app-mocked", "missing"];

function slugify(label: string) {
  return label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");
}

export function FlowEditorSheet({ flow, open, onOpenChange, onSaved, nextOrder }: FlowEditorSheetProps) {
  const [flowId, setFlowId] = useState("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [order, setOrder] = useState(0);
  const [status, setStatus] = useState<FlowStatus>("missing");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (flow) {
      setFlowId(flow.id);
      setTitle(flow.data.title);
      setDescription(flow.data.description);
      setOrder(flow.data.order);
      setStatus(flow.data.status);
    } else {
      setFlowId("");
      setTitle("");
      setDescription("");
      setOrder(nextOrder);
      setStatus("missing");
    }
  }, [flow, open, nextOrder]);

  async function handleSave() {
    if (!title) return;
    setSaving(true);
    try {
      const id = flowId || slugify(title) || crypto.randomUUID();
      const now = flowsRepo.serverTimestamp();
      await flowsRepo.create(id, {
        key: id,
        order,
        title,
        description,
        status,
        createdAt: (flow?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      });
      toast.success(flow ? "Flow updated" : "Flow created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save flow");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{flow ? `Edit ${flow.data.title}` : "New flow"}</SheetTitle>
          <SheetDescription>{flow ? `Key: ${flowId}` : "Key is auto-generated from the title."}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="flow-title">Title</Label>
            <Input id="flow-title" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="User Auth" />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="flow-description">Description</Label>
            <Textarea
              id="flow-description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={4}
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="flow-order">Order</Label>
              <Input id="flow-order" type="number" value={order} onChange={(e) => setOrder(Number(e.target.value))} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="flow-status">Status</Label>
              <Select value={status} onValueChange={(v) => setStatus((v as FlowStatus) ?? "missing")}>
                <SelectTrigger id="flow-status" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {STATUSES.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !title}>
            {saving ? "Saving…" : flow ? "Save changes" : "Create flow"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
