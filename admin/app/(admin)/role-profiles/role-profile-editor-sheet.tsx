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
import { roleProfilesRepo } from "@/services/repositories";
import type { RoleProfileDoc, RoleProfileKey, WithId } from "@/types/firestore";

interface RoleProfileEditorSheetProps {
  profile: WithId<RoleProfileDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

type FieldType = "text" | "number" | "csv";
interface FieldConfig {
  key: string;
  label: string;
  type: FieldType;
}

const ROLE_LABELS: Record<RoleProfileKey, string> = {
  singer: "Singer",
  musician: "Musician",
  lyricist: "Lyricist",
  music_director: "Music Director",
  composer: "Composer",
  sound_engineer: "Sound Engineer",
  dj: "DJ",
  rj: "RJ",
  podcaster: "Podcaster",
};

const ROLE_KEYS = Object.keys(ROLE_LABELS) as RoleProfileKey[];

const ROLE_FIELDS: Record<RoleProfileKey, FieldConfig[]> = {
  singer: [
    { key: "vocalRange", label: "Vocal range", type: "text" },
    { key: "singingStyles", label: "Singing styles (comma-separated)", type: "csv" },
    { key: "originalSongsCount", label: "Original songs count", type: "number" },
    { key: "coversCount", label: "Covers count", type: "number" },
    { key: "audioSampleUrls", label: "Audio sample URLs (comma-separated)", type: "csv" },
    { key: "videoSampleUrls", label: "Video sample URLs (comma-separated)", type: "csv" },
  ],
  musician: [
    { key: "primaryInstrument", label: "Primary instrument", type: "text" },
    { key: "secondaryInstruments", label: "Secondary instruments (comma-separated)", type: "csv" },
    { key: "yearsOfExperience", label: "Years of experience", type: "number" },
    { key: "performanceVideoUrls", label: "Performance video URLs (comma-separated)", type: "csv" },
    { key: "sessionRecordingUrls", label: "Session recording URLs (comma-separated)", type: "csv" },
  ],
  lyricist: [
    { key: "specializations", label: "Specializations (comma-separated)", type: "csv" },
    { key: "publishedWorksCount", label: "Published works count", type: "number" },
    { key: "writingSampleUrls", label: "Writing sample URLs (comma-separated)", type: "csv" },
    { key: "portfolioPdfUrls", label: "Portfolio PDF URLs (comma-separated)", type: "csv" },
  ],
  music_director: [
    { key: "specializations", label: "Specializations (comma-separated)", type: "csv" },
    { key: "filmography", label: "Filmography (comma-separated)", type: "csv" },
    { key: "discography", label: "Discography (comma-separated)", type: "csv" },
    { key: "showcaseAudioUrls", label: "Showcase audio URLs (comma-separated)", type: "csv" },
    { key: "showcaseVideoUrls", label: "Showcase video URLs (comma-separated)", type: "csv" },
  ],
  composer: [
    { key: "compositionStyles", label: "Composition styles (comma-separated)", type: "csv" },
    { key: "showcaseAudioUrls", label: "Showcase audio URLs (comma-separated)", type: "csv" },
  ],
  sound_engineer: [
    { key: "specialty", label: "Specialty (Recording/Mixing/Mastering/Live Sound)", type: "text" },
    { key: "dawStack", label: "DAW stack (comma-separated)", type: "csv" },
    { key: "ownedEquipment", label: "Owned equipment (comma-separated)", type: "csv" },
    { key: "pastProjectUrls", label: "Past project URLs (comma-separated)", type: "csv" },
  ],
  dj: [
    { key: "djType", label: "DJ type", type: "text" },
    { key: "eventExperienceYears", label: "Event experience (years)", type: "number" },
    { key: "mixUrls", label: "Mix URLs (comma-separated)", type: "csv" },
    { key: "eventVideoUrls", label: "Event video URLs (comma-separated)", type: "csv" },
  ],
  rj: [
    { key: "showsHosted", label: "Shows hosted (comma-separated)", type: "csv" },
    { key: "experienceYears", label: "Experience (years)", type: "number" },
    { key: "audioSampleUrls", label: "Audio sample URLs (comma-separated)", type: "csv" },
    { key: "showClipUrls", label: "Show clip URLs (comma-separated)", type: "csv" },
  ],
  podcaster: [
    { key: "podcastName", label: "Podcast name", type: "text" },
    { key: "categories", label: "Categories (comma-separated)", type: "csv" },
    { key: "listenerBase", label: "Listener base", type: "number" },
    { key: "episodeUrls", label: "Episode URLs (comma-separated)", type: "csv" },
    { key: "clipUrls", label: "Clip URLs (comma-separated)", type: "csv" },
  ],
};

function csv(value: unknown): string {
  return Array.isArray(value) ? value.join(", ") : "";
}

function fromCsv(text: string): string[] {
  return text
    .split(",")
    .map((v) => v.trim())
    .filter(Boolean);
}

export function RoleProfileEditorSheet({ profile, open, onOpenChange, onSaved }: RoleProfileEditorSheetProps) {
  const [docId, setDocId] = useState("");
  const [ownerUid, setOwnerUid] = useState("");
  const [roleKey, setRoleKey] = useState<RoleProfileKey>("singer");
  const [fields, setFields] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const isEditing = !!profile;

  useEffect(() => {
    if (profile) {
      setDocId(profile.id);
      setOwnerUid(profile.data.ownerUid);
      setRoleKey(profile.data.roleKey);
      const raw: Record<string, unknown> = { ...profile.data };
      const text: Record<string, string> = {};
      for (const f of ROLE_FIELDS[profile.data.roleKey]) {
        text[f.key] = f.type === "csv" ? csv(raw[f.key]) : raw[f.key] != null ? String(raw[f.key]) : "";
      }
      setFields(text);
    } else {
      setDocId("");
      setOwnerUid("");
      setRoleKey("singer");
      setFields({});
    }
  }, [profile, open]);

  async function handleSave() {
    if (!ownerUid) return;
    setSaving(true);
    try {
      const id = docId || `${ownerUid}_${roleKey}`;
      const now = roleProfilesRepo.serverTimestamp();
      const data: Record<string, unknown> = {
        ownerUid,
        roleKey,
        createdAt: (profile?.data.createdAt ?? now) as never,
        updatedAt: now as never,
      };
      for (const f of ROLE_FIELDS[roleKey]) {
        const raw = fields[f.key] ?? "";
        if (!raw) continue;
        data[f.key] = f.type === "csv" ? fromCsv(raw) : f.type === "number" ? Number(raw) : raw;
      }
      await roleProfilesRepo.create(id, data as unknown as RoleProfileDoc);
      toast.success(isEditing ? "Role profile updated" : "Role profile created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save role profile");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{isEditing ? `Edit ${ROLE_LABELS[roleKey]} profile` : "New role profile"}</SheetTitle>
          <SheetDescription>
            {isEditing
              ? `Doc ID: ${docId}`
              : "Doc ID is composed as \"{ownerUid}_{roleKey}\" — a user can hold one profile per role, multiple roles simultaneously."}
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="rp-owner">Owner UID</Label>
              <Input id="rp-owner" value={ownerUid} onChange={(e) => setOwnerUid(e.target.value)} disabled={isEditing} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="rp-role">Role</Label>
              <Select
                value={roleKey}
                onValueChange={(v) => setRoleKey((v as RoleProfileKey) ?? "singer")}
                disabled={isEditing}
              >
                <SelectTrigger id="rp-role" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {ROLE_KEYS.map((k) => (
                    <SelectItem key={k} value={k}>
                      {ROLE_LABELS[k]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-3 rounded-lg border border-border p-3">
            <p className="text-xs font-medium text-muted-foreground">{ROLE_LABELS[roleKey]} fields</p>
            {ROLE_FIELDS[roleKey].map((f) => (
              <div key={f.key} className="space-y-1.5">
                <Label htmlFor={`rp-${f.key}`}>{f.label}</Label>
                <Input
                  id={`rp-${f.key}`}
                  type={f.type === "number" ? "number" : "text"}
                  value={fields[f.key] ?? ""}
                  onChange={(e) => setFields((prev) => ({ ...prev, [f.key]: e.target.value }))}
                />
              </div>
            ))}
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !ownerUid}>
            {saving ? "Saving…" : isEditing ? "Save changes" : "Create profile"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
