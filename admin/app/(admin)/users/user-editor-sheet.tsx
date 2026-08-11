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
import { Textarea } from "@/components/ui/textarea";
import { CappedCheckboxList } from "@/components/shared/capped-checkbox-list";
import { useTaxonomyOptions } from "@/hooks/use-taxonomy-options";
import { usersRepo } from "@/services/repositories";
import type { UserDoc, WithId } from "@/types/firestore";

interface UserEditorSheetProps {
  /** Existing user to edit, or null to create a new one. */
  user: WithId<UserDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const MAX_GENRES = 3;

type ArrayField = "genres" | "languages" | "instruments" | "personaBadges" | "trustBadges";
const ARRAY_FIELDS: ArrayField[] = ["genres", "languages", "instruments", "personaBadges", "trustBadges"];

function emptyForm(): UserDoc {
  return {
    displayName: "",
    email: "",
    primaryRole: "",
    secondaryRole: "",
    isVerified: false,
    isBanned: false,
    isAvailable: true,
    hourlyRate: "",
    location: "",
    bio: "",
    genres: [],
    languages: [],
    instruments: [],
    personaBadges: [],
    trustBadges: [],
    avatarUrl: "",
    createdAt: usersRepo.serverTimestamp() as never,
  };
}

/** Normalizes a Firestore doc that may predate this session's schema — array fields can be
 * entirely absent on real records, and `.includes()`/`.length` on undefined would crash the form. */
function normalizeUserDoc(data: UserDoc): UserDoc {
  const normalized = { ...data };
  for (const field of ARRAY_FIELDS) {
    if (!Array.isArray(normalized[field])) normalized[field] = [];
  }
  return normalized;
}

/** Freeform fallback text for each array field, used only until its master-data category is seeded. */
function emptyFallbackText(): Record<ArrayField, string> {
  return { genres: "", languages: "", instruments: "", personaBadges: "", trustBadges: "" };
}

export function UserEditorSheet({ user, open, onOpenChange, onSaved }: UserEditorSheetProps) {
  const [uid, setUid] = useState("");
  const [form, setForm] = useState<UserDoc>(emptyForm());
  const [fallbackText, setFallbackText] = useState<Record<ArrayField, string>>(emptyFallbackText());
  const [saving, setSaving] = useState(false);

  const roleOptions = useTaxonomyOptions("person_types");
  const genreOptions = useTaxonomyOptions("genres");
  const languageOptions = useTaxonomyOptions("languages");
  const instrumentOptions = useTaxonomyOptions("instruments");
  const personaBadgeOptions = useTaxonomyOptions("persona_badges");
  const trustBadgeOptions = useTaxonomyOptions("trust_badges");

  useEffect(() => {
    if (user) {
      setUid(user.id);
      const normalized = normalizeUserDoc(user.data);
      setForm(normalized);
      setFallbackText({
        genres: normalized.genres.join(", "),
        languages: normalized.languages.join(", "),
        instruments: normalized.instruments.join(", "),
        personaBadges: normalized.personaBadges.join(", "),
        trustBadges: normalized.trustBadges.join(", "),
      });
    } else {
      setUid(crypto.randomUUID());
      setForm(emptyForm());
      setFallbackText(emptyFallbackText());
    }
  }, [user, open]);

  function toggleArrayValue(field: ArrayField, value: string, max?: number) {
    setForm((f) => {
      const arr = f[field];
      const has = arr.includes(value);
      if (has) return { ...f, [field]: arr.filter((v) => v !== value) };
      if (max && arr.length >= max) return f;
      return { ...f, [field]: [...arr, value] };
    });
  }

  function fallbackToArray(text: string) {
    return text
      .split(",")
      .map((v) => v.trim())
      .filter(Boolean);
  }

  async function handleSave() {
    if (!form.displayName || !form.email || !form.primaryRole) return;
    setSaving(true);
    try {
      const resolved: UserDoc = {
        ...form,
        genres: genreOptions ? form.genres : fallbackToArray(fallbackText.genres),
        languages: languageOptions ? form.languages : fallbackToArray(fallbackText.languages),
        instruments: instrumentOptions ? form.instruments : fallbackToArray(fallbackText.instruments),
        personaBadges: personaBadgeOptions ? form.personaBadges : fallbackToArray(fallbackText.personaBadges),
        trustBadges: trustBadgeOptions ? form.trustBadges : fallbackToArray(fallbackText.trustBadges),
      };
      await usersRepo.create(uid, resolved);
      toast.success(user ? "User updated" : "User created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save user");
    } finally {
      setSaving(false);
    }
  }

  function arrayField(key: ArrayField, label: string, options: ReturnType<typeof useTaxonomyOptions>, max?: number) {
    if (options) {
      return (
        <CappedCheckboxList
          label={label}
          options={options}
          selected={form[key]}
          onToggle={(value) => toggleArrayValue(key, value, max)}
          max={max}
        />
      );
    }
    return (
      <div className="space-y-1.5">
        <Label htmlFor={`user-${key}`}>{label} (comma-separated)</Label>
        <Input
          id={`user-${key}`}
          value={fallbackText[key]}
          onChange={(e) => setFallbackText((t) => ({ ...t, [key]: e.target.value }))}
        />
        <p className="text-xs text-muted-foreground">
          No matching master-data category yet — using freeform text. Create one under Master Data to
          switch to a controlled list here.
        </p>
      </div>
    );
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{user ? `Edit ${user.data.displayName}` : "New user"}</SheetTitle>
          <SheetDescription>
            {user
              ? `UID: ${uid}`
              : "This creates a Firestore profile document only — not a real, sign-in-capable Firebase Auth account. Useful for seeding test data; a real person still has to sign up through the app itself."}
          </SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="user-name">Display name</Label>
              <Input
                id="user-name"
                value={form.displayName}
                onChange={(e) => setForm((f) => ({ ...f, displayName: e.target.value }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="user-email">Email</Label>
              <Input
                id="user-email"
                type="email"
                value={form.email}
                onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              />
            </div>
          </div>

          {roleOptions ? (
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="user-primary-role">Primary role</Label>
                <Select
                  value={form.primaryRole}
                  onValueChange={(v) => setForm((f) => ({ ...f, primaryRole: v ?? "" }))}
                >
                  <SelectTrigger id="user-primary-role" className="w-full">
                    <SelectValue placeholder="Select a role" />
                  </SelectTrigger>
                  <SelectContent>
                    {roleOptions.map((r) => (
                      <SelectItem key={r.id} value={r.data.value}>
                        {r.data.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-secondary-role">Secondary role (optional)</Label>
                <Select
                  value={form.secondaryRole || "__none__"}
                  onValueChange={(v) => setForm((f) => ({ ...f, secondaryRole: !v || v === "__none__" ? "" : v }))}
                >
                  <SelectTrigger id="user-secondary-role" className="w-full">
                    <SelectValue placeholder="None" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="__none__">None</SelectItem>
                    {roleOptions
                      .filter((r) => r.data.value !== form.primaryRole)
                      .map((r) => (
                        <SelectItem key={r.id} value={r.data.value}>
                          {r.data.label}
                        </SelectItem>
                      ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="user-primary-role">Primary role</Label>
                <Input
                  id="user-primary-role"
                  value={form.primaryRole}
                  onChange={(e) => setForm((f) => ({ ...f, primaryRole: e.target.value }))}
                  placeholder="Singer / Vocalist"
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-secondary-role">Secondary role (optional)</Label>
                <Input
                  id="user-secondary-role"
                  value={form.secondaryRole}
                  onChange={(e) => setForm((f) => ({ ...f, secondaryRole: e.target.value }))}
                />
              </div>
            </div>
          )}
          {!roleOptions ? (
            <p className="-mt-2 text-xs text-muted-foreground">
              No &quot;person_types&quot; master-data category yet — using freeform text.
            </p>
          ) : null}

          <div className="space-y-1.5">
            <Label htmlFor="user-location">Location</Label>
            <Input
              id="user-location"
              value={form.location}
              onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))}
            />
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="user-bio">Bio</Label>
            <Textarea
              id="user-bio"
              value={form.bio}
              onChange={(e) => setForm((f) => ({ ...f, bio: e.target.value }))}
              rows={3}
            />
          </div>

          {arrayField("genres", "Genres", genreOptions, MAX_GENRES)}
          {arrayField("languages", "Languages", languageOptions)}
          {arrayField("instruments", "Instruments", instrumentOptions)}
          {arrayField("personaBadges", "Persona badges", personaBadgeOptions)}
          {arrayField("trustBadges", "Trust badges (admin-granted)", trustBadgeOptions)}

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="user-rate">Hourly rate</Label>
              <Input
                id="user-rate"
                value={form.hourlyRate}
                onChange={(e) => setForm((f) => ({ ...f, hourlyRate: e.target.value }))}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="user-avatar">Avatar URL</Label>
              <Input
                id="user-avatar"
                value={form.avatarUrl}
                onChange={(e) => setForm((f) => ({ ...f, avatarUrl: e.target.value }))}
              />
            </div>
          </div>

          <div className="flex flex-wrap gap-4 pt-1">
            <label className="flex items-center gap-2 text-sm">
              <Checkbox
                checked={form.isVerified}
                onCheckedChange={(c) => setForm((f) => ({ ...f, isVerified: c === true }))}
              />
              Verified
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Checkbox
                checked={form.isBanned}
                onCheckedChange={(c) => setForm((f) => ({ ...f, isBanned: c === true }))}
              />
              Banned
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Checkbox
                checked={form.isAvailable}
                onCheckedChange={(c) => setForm((f) => ({ ...f, isAvailable: c === true }))}
              />
              Available
            </label>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !form.displayName || !form.email || !form.primaryRole}>
            {saving ? "Saving…" : user ? "Save changes" : "Create user"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
