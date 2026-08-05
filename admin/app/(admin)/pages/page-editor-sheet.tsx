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
import { useAuth } from "@/hooks/use-auth";
import { pageContentRepo } from "@/services/repositories";
import type { PageContentDoc, PageStatus, WithId } from "@/types/firestore";

interface PageEditorSheetProps {
  /** Existing page to edit, or null to create a new one. */
  page: WithId<PageContentDoc> | null;
  /** When creating, prefill the slug field — undefined means "new page" flow with an editable slug. */
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const EMPTY: PageContentDoc = {
  route: "",
  title: "",
  status: "DRAFT",
  content: "",
  seoDescription: "",
  updatedBy: "",
  updatedAt: pageContentRepo.serverTimestamp() as never,
};

export function PageEditorSheet({ page, open, onOpenChange, onSaved }: PageEditorSheetProps) {
  const { user } = useAuth();
  const [slug, setSlug] = useState("");
  const [form, setForm] = useState<PageContentDoc>(EMPTY);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (page) {
      setSlug(page.id);
      setForm(page.data);
    } else {
      setSlug("");
      setForm(EMPTY);
    }
  }, [page, open]);

  async function handleSave() {
    if (!slug || !form.title || !form.route || !user) return;
    setSaving(true);
    try {
      await pageContentRepo.create(slug, {
        ...form,
        updatedBy: user.uid,
        updatedAt: pageContentRepo.serverTimestamp() as never,
      });
      toast.success(page ? "Page updated" : "Page created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save page");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{page ? `Edit ${page.data.title}` : "New page"}</SheetTitle>
          <SheetDescription>
            This saves to Firestore only. website/ is still static HTML and doesn&apos;t read this
            collection yet — publishing here doesn&apos;t change what&apos;s live at musiclinkd.com until
            that&apos;s wired up separately.
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="space-y-1.5">
            <Label htmlFor="page-slug">Slug (document ID)</Label>
            <Input
              id="page-slug"
              value={slug}
              onChange={(e) => setSlug(e.target.value)}
              disabled={!!page}
              placeholder="e.g. about, legal-privacy"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="page-route">Route</Label>
            <Input
              id="page-route"
              value={form.route}
              onChange={(e) => setForm((f) => ({ ...f, route: e.target.value }))}
              placeholder="/about"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="page-title">Title</Label>
            <Input
              id="page-title"
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="page-status">Status</Label>
            <Select value={form.status} onValueChange={(v) => setForm((f) => ({ ...f, status: v as PageStatus }))}>
              <SelectTrigger id="page-status" className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="DRAFT">Draft</SelectItem>
                <SelectItem value="PUBLISHED">Published</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="page-content">Content</Label>
            <Textarea
              id="page-content"
              value={form.content}
              onChange={(e) => setForm((f) => ({ ...f, content: e.target.value }))}
              rows={8}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="page-seo">SEO description</Label>
            <Textarea
              id="page-seo"
              value={form.seoDescription}
              onChange={(e) => setForm((f) => ({ ...f, seoDescription: e.target.value }))}
              rows={2}
            />
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !slug || !form.title || !form.route}>
            {saving ? "Saving…" : "Save page"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
