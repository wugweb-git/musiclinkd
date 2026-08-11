"use client";

import { useEffect, useState } from "react";
import { Trash2 } from "lucide-react";
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
import { Textarea } from "@/components/ui/textarea";
import { CappedCheckboxList } from "@/components/shared/capped-checkbox-list";
import { useTaxonomyOptions } from "@/hooks/use-taxonomy-options";
import { portfolioProjectsRepo } from "@/services/repositories";
import type {
  CreditItem,
  PortfolioProjectDoc,
  PortfolioProjectStatus,
  PortfolioVisibility,
  ProjectCollaborator,
  WithId,
} from "@/types/firestore";

interface PortfolioEditorSheetProps {
  project: WithId<PortfolioProjectDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const STATUSES: PortfolioProjectStatus[] = ["Draft", "In Production", "Upcoming", "Released"];
const VISIBILITIES: PortfolioVisibility[] = ["Public", "Connections Only", "Private Draft"];
const LINK_FIELDS: (keyof Pick<
  PortfolioProjectDoc,
  "spotifyUrl" | "appleMusicUrl" | "youtubeUrl" | "soundcloudUrl" | "mixcloudUrl" | "instagramReelUrl"
>)[] = ["spotifyUrl", "appleMusicUrl", "youtubeUrl", "soundcloudUrl", "mixcloudUrl", "instagramReelUrl"];

function emptyForm(): PortfolioProjectDoc {
  return {
    ownerUid: "",
    name: "",
    description: "",
    type: "",
    releaseDate: "",
    status: "Draft",
    visibility: "Private Draft",
    ownerRoles: [],
    collaborators: [],
    credits: [],
    artworkTheme: "Default",
    projectViews: 0,
    likes: 0,
    shares: 0,
    createdAt: portfolioProjectsRepo.serverTimestamp() as never,
    lastUpdatedAt: portfolioProjectsRepo.serverTimestamp() as never,
  };
}

export function PortfolioEditorSheet({ project, open, onOpenChange, onSaved }: PortfolioEditorSheetProps) {
  const [projectId, setProjectId] = useState("");
  const [form, setForm] = useState<PortfolioProjectDoc>(emptyForm());
  const [saving, setSaving] = useState(false);

  const typeOptions = useTaxonomyOptions("project_types");
  const roleOptions = useTaxonomyOptions("project_roles");

  useEffect(() => {
    if (project) {
      setProjectId(project.id);
      setForm({ ...emptyForm(), ...project.data, ownerRoles: project.data.ownerRoles ?? [] });
    } else {
      setProjectId(crypto.randomUUID());
      setForm(emptyForm());
    }
  }, [project, open]);

  function toggleOwnerRole(value: string) {
    setForm((f) => {
      const has = f.ownerRoles.includes(value);
      return { ...f, ownerRoles: has ? f.ownerRoles.filter((r) => r !== value) : [...f.ownerRoles, value] };
    });
  }

  function addCollaborator() {
    setForm((f) => ({
      ...f,
      collaborators: [...f.collaborators, { id: crypto.randomUUID(), name: "", role: "", avatarEmoji: "🎵", isVerified: false }],
    }));
  }

  function updateCollaborator(index: number, patch: Partial<ProjectCollaborator>) {
    setForm((f) => ({
      ...f,
      collaborators: f.collaborators.map((c, i) => (i === index ? { ...c, ...patch } : c)),
    }));
  }

  function removeCollaborator(index: number) {
    setForm((f) => ({ ...f, collaborators: f.collaborators.filter((_, i) => i !== index) }));
  }

  function addCredit() {
    setForm((f) => ({ ...f, credits: [...f.credits, { collaboratorName: "", role: "", avatarEmoji: "🎵" }] }));
  }

  function updateCredit(index: number, patch: Partial<CreditItem>) {
    setForm((f) => ({ ...f, credits: f.credits.map((c, i) => (i === index ? { ...c, ...patch } : c)) }));
  }

  function removeCredit(index: number) {
    setForm((f) => ({ ...f, credits: f.credits.filter((_, i) => i !== index) }));
  }

  async function handleSave() {
    if (!form.name || !form.ownerUid) return;
    setSaving(true);
    try {
      await portfolioProjectsRepo.create(projectId, {
        ...form,
        lastUpdatedAt: portfolioProjectsRepo.serverTimestamp() as never,
      });
      toast.success(project ? "Project updated" : "Project created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save project");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{project ? `Edit ${project.data.name}` : "New portfolio project"}</SheetTitle>
          <SheetDescription>
            {project ? `Doc ID: ${projectId}` : "Admin-seeded/test project — real projects are published from the app."}
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="pp-name">Name</Label>
              <Input id="pp-name" value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="pp-owner">Owner UID</Label>
              <Input
                id="pp-owner"
                value={form.ownerUid}
                onChange={(e) => setForm((f) => ({ ...f, ownerUid: e.target.value }))}
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="pp-description">Description</Label>
            <Textarea
              id="pp-description"
              value={form.description}
              onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
              rows={2}
            />
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="pp-type">Type</Label>
              {typeOptions ? (
                <Select value={form.type} onValueChange={(v) => setForm((f) => ({ ...f, type: v ?? "" }))}>
                  <SelectTrigger id="pp-type" className="w-full">
                    <SelectValue placeholder="Select" />
                  </SelectTrigger>
                  <SelectContent>
                    {typeOptions.map((t) => (
                      <SelectItem key={t.id} value={t.data.value}>
                        {t.data.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              ) : (
                <Input id="pp-type" value={form.type} onChange={(e) => setForm((f) => ({ ...f, type: e.target.value }))} placeholder="song" />
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="pp-status">Status</Label>
              <Select
                value={form.status}
                onValueChange={(v) => setForm((f) => ({ ...f, status: (v as PortfolioProjectStatus) ?? "Draft" }))}
              >
                <SelectTrigger id="pp-status" className="w-full">
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
            <div className="space-y-1.5">
              <Label htmlFor="pp-visibility">Visibility</Label>
              <Select
                value={form.visibility}
                onValueChange={(v) => setForm((f) => ({ ...f, visibility: (v as PortfolioVisibility) ?? "Private Draft" }))}
              >
                <SelectTrigger id="pp-visibility" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {VISIBILITIES.map((v) => (
                    <SelectItem key={v} value={v}>
                      {v}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="pp-release-date">Release date</Label>
              <Input
                id="pp-release-date"
                value={form.releaseDate}
                onChange={(e) => setForm((f) => ({ ...f, releaseDate: e.target.value }))}
                placeholder="2026-08-11"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="pp-artwork">Artwork theme (palette keyword, not an image URL)</Label>
              <Input
                id="pp-artwork"
                value={form.artworkTheme}
                onChange={(e) => setForm((f) => ({ ...f, artworkTheme: e.target.value }))}
                placeholder="Acoustic"
              />
            </div>
          </div>

          {roleOptions ? (
            <CappedCheckboxList label="Owner roles" options={roleOptions} selected={form.ownerRoles} onToggle={toggleOwnerRole} />
          ) : null}

          <div className="space-y-1.5 rounded-lg border border-border p-3">
            <p className="text-xs font-medium text-muted-foreground">Streaming links</p>
            <div className="grid grid-cols-2 gap-3">
              {LINK_FIELDS.map((key) => (
                <div key={key} className="space-y-1.5">
                  <Label htmlFor={`pp-${key}`} className="capitalize">
                    {key.replace("Url", "").replace(/([A-Z])/g, " $1")}
                  </Label>
                  <Input
                    id={`pp-${key}`}
                    value={form[key] ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, [key]: e.target.value }))}
                  />
                </div>
              ))}
            </div>
          </div>

          <div className="space-y-1.5">
            <Label>Collaborators</Label>
            {form.collaborators.map((c, i) => (
              <div key={c.id || i} className="flex items-center gap-2">
                <Input value={c.name} onChange={(e) => updateCollaborator(i, { name: e.target.value })} placeholder="Name" className="flex-1" />
                <Input value={c.role} onChange={(e) => updateCollaborator(i, { role: e.target.value })} placeholder="Role" className="flex-1" />
                <label className="flex items-center gap-1 whitespace-nowrap text-xs">
                  <Checkbox checked={c.isVerified} onCheckedChange={(v) => updateCollaborator(i, { isVerified: v === true })} />
                  Verified
                </label>
                <Button type="button" size="icon-sm" variant="ghost" onClick={() => removeCollaborator(i)}>
                  <Trash2 className="size-3.5" />
                </Button>
              </div>
            ))}
            <Button type="button" size="sm" variant="outline" onClick={addCollaborator}>
              Add collaborator
            </Button>
          </div>

          <div className="space-y-1.5">
            <Label>Credits</Label>
            {form.credits.map((c, i) => (
              <div key={i} className="flex items-center gap-2">
                <Input
                  value={c.collaboratorName}
                  onChange={(e) => updateCredit(i, { collaboratorName: e.target.value })}
                  placeholder="Name"
                  className="flex-1"
                />
                <Input value={c.role} onChange={(e) => updateCredit(i, { role: e.target.value })} placeholder="Credited role" className="flex-1" />
                <Button type="button" size="icon-sm" variant="ghost" onClick={() => removeCredit(i)}>
                  <Trash2 className="size-3.5" />
                </Button>
              </div>
            ))}
            <Button type="button" size="sm" variant="outline" onClick={addCredit}>
              Add credit
            </Button>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="pp-views">Views</Label>
              <Input
                id="pp-views"
                type="number"
                value={form.projectViews}
                onChange={(e) => setForm((f) => ({ ...f, projectViews: Number(e.target.value) }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="pp-likes">Likes</Label>
              <Input
                id="pp-likes"
                type="number"
                value={form.likes}
                onChange={(e) => setForm((f) => ({ ...f, likes: Number(e.target.value) }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="pp-shares">Shares</Label>
              <Input
                id="pp-shares"
                type="number"
                value={form.shares}
                onChange={(e) => setForm((f) => ({ ...f, shares: Number(e.target.value) }))}
              />
            </div>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !form.name || !form.ownerUid}>
            {saving ? "Saving…" : project ? "Save changes" : "Create project"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
