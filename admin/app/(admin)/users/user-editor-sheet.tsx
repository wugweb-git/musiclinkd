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
import { usersRepo } from "@/services/repositories";
import type {
  BandLineupMember,
  BusinessServiceOffering,
  BusinessTeamMember,
  BusinessType,
  ProfileType,
  UserDoc,
  WithId,
} from "@/types/firestore";

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

const PROFILE_TYPES: ProfileType[] = ["TALENT", "BAND", "BUSINESS", "VENUE"];
const BUSINESS_TYPES: BusinessType[] = ["Studio", "Label", "School", "Dealer"];

/** Plain string[] fields with no taxonomy backing — comma-separated text, parsed on save. */
type CsvField =
  | "services"
  | "equipmentList"
  | "venueFeatures"
  | "eventCalendar"
  | "dawStack"
  | "writingSamples"
  | "galleryUrls";

const EXTERNAL_LINK_FIELDS: (keyof NonNullable<UserDoc["externalLinks"]>)[] = [
  "spotify",
  "appleMusic",
  "youtube",
  "soundcloud",
  "mixcloud",
  "instagram",
  "twitter",
  "linkedin",
  "bandcamp",
  "website",
];

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
  const [csvText, setCsvText] = useState<Record<CsvField, string>>({
    services: "",
    equipmentList: "",
    venueFeatures: "",
    eventCalendar: "",
    dawStack: "",
    writingSamples: "",
    galleryUrls: "",
  });
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
      setCsvText({
        services: (normalized.services ?? []).join(", "),
        equipmentList: (normalized.equipmentList ?? []).join(", "),
        venueFeatures: (normalized.venueFeatures ?? []).join(", "),
        eventCalendar: (normalized.eventCalendar ?? []).join(", "),
        dawStack: (normalized.dawStack ?? []).join(", "),
        writingSamples: (normalized.writingSamples ?? []).join(", "),
        galleryUrls: (normalized.galleryUrls ?? []).join(", "),
      });
    } else {
      setUid(crypto.randomUUID());
      setForm(emptyForm());
      setFallbackText(emptyFallbackText());
      setCsvText({
        services: "",
        equipmentList: "",
        venueFeatures: "",
        eventCalendar: "",
        dawStack: "",
        writingSamples: "",
        galleryUrls: "",
      });
    }
  }, [user, open]);

  function addLineupMember() {
    setForm((f) => ({
      ...f,
      lineupMembers: [...(f.lineupMembers ?? []), { name: "", role: "", isLeader: false }],
    }));
  }

  function updateLineupMember(index: number, patch: Partial<BandLineupMember>) {
    setForm((f) => ({
      ...f,
      lineupMembers: (f.lineupMembers ?? []).map((m, i) => (i === index ? { ...m, ...patch } : m)),
    }));
  }

  function removeLineupMember(index: number) {
    setForm((f) => ({
      ...f,
      lineupMembers: (f.lineupMembers ?? []).filter((_, i) => i !== index),
    }));
  }

  function addTeamMember() {
    setForm((f) => ({ ...f, teamMembers: [...(f.teamMembers ?? []), { name: "", role: "" }] }));
  }

  function updateTeamMember(index: number, patch: Partial<BusinessTeamMember>) {
    setForm((f) => ({
      ...f,
      teamMembers: (f.teamMembers ?? []).map((m, i) => (i === index ? { ...m, ...patch } : m)),
    }));
  }

  function removeTeamMember(index: number) {
    setForm((f) => ({ ...f, teamMembers: (f.teamMembers ?? []).filter((_, i) => i !== index) }));
  }

  function addServiceOffering() {
    setForm((f) => ({ ...f, serviceOfferings: [...(f.serviceOfferings ?? []), { title: "" }] }));
  }

  function updateServiceOffering(index: number, patch: Partial<BusinessServiceOffering>) {
    setForm((f) => ({
      ...f,
      serviceOfferings: (f.serviceOfferings ?? []).map((s, i) => (i === index ? { ...s, ...patch } : s)),
    }));
  }

  function removeServiceOffering(index: number) {
    setForm((f) => ({ ...f, serviceOfferings: (f.serviceOfferings ?? []).filter((_, i) => i !== index) }));
  }

  function toggleArrayValue(field: ArrayField, value: string, max?: number) {
    setForm((f) => {
      const arr = f[field];
      const has = arr.includes(value);
      if (has) return { ...f, [field]: arr.filter((v) => v !== value) };
      if (max && arr.length >= max) return f;
      return { ...f, [field]: [...arr, value] };
    });
  }

  const MAX_SUPPORTING_ROLES = 2;

  function toggleSupportingRole(value: string) {
    setForm((f) => {
      const arr = f.supportingRoles ?? [];
      const has = arr.includes(value);
      if (has) return { ...f, supportingRoles: arr.filter((v) => v !== value) };
      if (arr.length >= MAX_SUPPORTING_ROLES) return f;
      return { ...f, supportingRoles: [...arr, value] };
    });
  }

  /**
   * person_types options tagged with metadata.applicableProfileTypes are
   * restricted to those profile types; untagged options show for everyone.
   * Prevents impossible combinations (e.g. a TALENT profile picking a
   * Business-only role) — see the plan's Part O.
   */
  function roleOptionsFor(profileType: ProfileType | undefined) {
    if (!roleOptions) return roleOptions;
    if (!profileType) return roleOptions;
    return roleOptions.filter((r) => {
      const applicable = r.data.metadata?.applicableProfileTypes as string[] | undefined;
      return !applicable || applicable.length === 0 || applicable.includes(profileType);
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
        ...(form.profileType === "BUSINESS"
          ? {
              services: fallbackToArray(csvText.services),
              equipmentList: fallbackToArray(csvText.equipmentList),
              galleryUrls: fallbackToArray(csvText.galleryUrls),
            }
          : {}),
        ...(form.profileType === "VENUE"
          ? {
              venueFeatures: fallbackToArray(csvText.venueFeatures),
              eventCalendar: fallbackToArray(csvText.eventCalendar),
            }
          : {}),
        ...(form.profileType === "TALENT"
          ? {
              dawStack: fallbackToArray(csvText.dawStack),
              writingSamples: fallbackToArray(csvText.writingSamples),
            }
          : {}),
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
                    {(roleOptionsFor(form.profileType) ?? []).map((r) => (
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
                    {(roleOptionsFor(form.profileType) ?? [])
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

          {roleOptions ? (
            <CappedCheckboxList
              label="Supporting roles (proposed — not yet confirmed by AI Studio, see Prompt F §11)"
              options={roleOptionsFor(form.profileType) ?? []}
              selected={form.supportingRoles ?? []}
              onToggle={toggleSupportingRole}
              max={MAX_SUPPORTING_ROLES}
            />
          ) : null}

          <div className="space-y-1.5">
            <Label htmlFor="user-profile-type">Profile type</Label>
            <Select
              value={form.profileType ?? "__none__"}
              onValueChange={(v) =>
                setForm((f) => ({ ...f, profileType: v === "__none__" ? undefined : (v as ProfileType) }))
              }
            >
              <SelectTrigger id="user-profile-type" className="w-full">
                <SelectValue placeholder="Not set" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__none__">Not set</SelectItem>
                {PROFILE_TYPES.map((t) => (
                  <SelectItem key={t} value={t}>
                    {t.charAt(0) + t.slice(1).toLowerCase()}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              Write-locked after creation — a user can never change this on their own document once set
              (see firestore.rules). No confirmed app-side writer yet; set here for admin-seeded/test
              accounts.
            </p>
          </div>

          {form.profileType === "TALENT" ? (
            <div className="space-y-3 rounded-lg border border-border p-3">
              <p className="text-xs font-medium text-muted-foreground">
                Talent profile — role-specific fields (fill in only what applies to this person&apos;s role)
              </p>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="user-vocal-range">Vocal range (Singer)</Label>
                  <Input
                    id="user-vocal-range"
                    value={form.vocalRange ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, vocalRange: e.target.value }))}
                    placeholder="Tenor"
                  />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="user-session-availability">Session availability (Musician)</Label>
                  <Input
                    id="user-session-availability"
                    value={form.sessionAvailability ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, sessionAvailability: e.target.value }))}
                    placeholder="Available Now"
                  />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-daw-stack">DAW stack (Engineer/Editor, comma-separated)</Label>
                <Input
                  id="user-daw-stack"
                  value={csvText.dawStack}
                  onChange={(e) => setCsvText((c) => ({ ...c, dawStack: e.target.value }))}
                  placeholder="Pro Tools, Ableton Live"
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-performance-history">Performance history (DJ)</Label>
                <Textarea
                  id="user-performance-history"
                  value={form.performanceHistory ?? ""}
                  onChange={(e) => setForm((f) => ({ ...f, performanceHistory: e.target.value }))}
                  rows={2}
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-writing-samples">Writing samples (Lyricist, comma-separated URLs)</Label>
                <Input
                  id="user-writing-samples"
                  value={csvText.writingSamples}
                  onChange={(e) => setCsvText((c) => ({ ...c, writingSamples: e.target.value }))}
                />
              </div>
            </div>
          ) : null}

          <div className="space-y-1.5 rounded-lg border border-border p-3">
            <p className="text-xs font-medium text-muted-foreground">External links</p>
            <div className="grid grid-cols-2 gap-3">
              {EXTERNAL_LINK_FIELDS.map((key) => (
                <div key={key} className="space-y-1.5">
                  <Label htmlFor={`user-link-${key}`} className="capitalize">
                    {key === "appleMusic" ? "Apple Music" : key}
                  </Label>
                  <Input
                    id={`user-link-${key}`}
                    value={form.externalLinks?.[key] ?? ""}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        externalLinks: { ...f.externalLinks, [key]: e.target.value },
                      }))
                    }
                  />
                </div>
              ))}
            </div>
          </div>

          {form.profileType === "BUSINESS" ? (
            <div className="space-y-3 rounded-lg border border-border p-3">
              <p className="text-xs font-medium text-muted-foreground">Business profile</p>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="user-business-name">Business name</Label>
                  <Input
                    id="user-business-name"
                    value={form.businessName ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, businessName: e.target.value }))}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="user-business-type">Business type</Label>
                  <Select
                    value={form.businessType ?? "__none__"}
                    onValueChange={(v) =>
                      setForm((f) => ({ ...f, businessType: v === "__none__" ? undefined : (v as BusinessType) }))
                    }
                  >
                    <SelectTrigger id="user-business-type" className="w-full">
                      <SelectValue placeholder="Select" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="__none__">Not set</SelectItem>
                      {BUSINESS_TYPES.map((t) => (
                        <SelectItem key={t} value={t}>
                          {t}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-tagline">Tagline</Label>
                <Input
                  id="user-tagline"
                  value={form.tagline ?? ""}
                  onChange={(e) => setForm((f) => ({ ...f, tagline: e.target.value }))}
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="user-operating-hours">Operating hours</Label>
                  <Input
                    id="user-operating-hours"
                    value={form.operatingHours ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, operatingHours: e.target.value }))}
                    placeholder="Mon-Sat: 09:00 - 22:00"
                  />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="user-gstin">GSTIN</Label>
                  <Input
                    id="user-gstin"
                    value={form.gstin ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, gstin: e.target.value }))}
                  />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-business-address">Business address</Label>
                <Input
                  id="user-business-address"
                  value={form.businessAddress ?? ""}
                  onChange={(e) => setForm((f) => ({ ...f, businessAddress: e.target.value }))}
                />
              </div>
              <div className="space-y-1.5">
                <Label>Team members</Label>
                {(form.teamMembers ?? []).map((m, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <Input value={m.name} onChange={(e) => updateTeamMember(i, { name: e.target.value })} placeholder="Name" className="flex-1" />
                    <Input value={m.role} onChange={(e) => updateTeamMember(i, { role: e.target.value })} placeholder="Role" className="flex-1" />
                    <Button type="button" size="icon-sm" variant="ghost" onClick={() => removeTeamMember(i)}>
                      <Trash2 className="size-3.5" />
                    </Button>
                  </div>
                ))}
                <Button type="button" size="sm" variant="outline" onClick={addTeamMember}>
                  Add team member
                </Button>
              </div>

              <div className="space-y-1.5">
                <Label>Service offerings (priced menu)</Label>
                {(form.serviceOfferings ?? []).map((s, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <Input
                      value={s.title}
                      onChange={(e) => updateServiceOffering(i, { title: e.target.value })}
                      placeholder="Title"
                      className="flex-1"
                    />
                    <Input
                      value={s.price ?? ""}
                      onChange={(e) => updateServiceOffering(i, { price: e.target.value })}
                      placeholder="Price"
                      className="w-24"
                    />
                    <Input
                      value={s.duration ?? ""}
                      onChange={(e) => updateServiceOffering(i, { duration: e.target.value })}
                      placeholder="Duration"
                      className="w-24"
                    />
                    <Button type="button" size="icon-sm" variant="ghost" onClick={() => removeServiceOffering(i)}>
                      <Trash2 className="size-3.5" />
                    </Button>
                  </div>
                ))}
                <Button type="button" size="sm" variant="outline" onClick={addServiceOffering}>
                  Add service
                </Button>
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="user-gallery">Gallery photo URLs (comma-separated)</Label>
                <Input
                  id="user-gallery"
                  value={csvText.galleryUrls}
                  onChange={(e) => setCsvText((c) => ({ ...c, galleryUrls: e.target.value }))}
                />
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="user-services">Services (comma-separated)</Label>
                <Input
                  id="user-services"
                  value={csvText.services}
                  onChange={(e) => setCsvText((c) => ({ ...c, services: e.target.value }))}
                  placeholder="Recording, Mixing, Mastering"
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-equipment">Equipment (comma-separated)</Label>
                <Input
                  id="user-equipment"
                  value={csvText.equipmentList}
                  onChange={(e) => setCsvText((c) => ({ ...c, equipmentList: e.target.value }))}
                />
              </div>
            </div>
          ) : null}

          {form.profileType === "BAND" ? (
            <div className="space-y-3 rounded-lg border border-border p-3">
              <p className="text-xs font-medium text-muted-foreground">Band profile</p>
              <div className="space-y-1.5">
                <Label htmlFor="user-band-name">Band name</Label>
                <Input
                  id="user-band-name"
                  value={form.bandName ?? ""}
                  onChange={(e) => setForm((f) => ({ ...f, bandName: e.target.value }))}
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="user-tech-rider">Technical rider URL</Label>
                  <Input
                    id="user-tech-rider"
                    value={form.techRiderUrl ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, techRiderUrl: e.target.value }))}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="user-stage-plot">Stage plot URL</Label>
                  <Input
                    id="user-stage-plot"
                    value={form.stagePlotUrl ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, stagePlotUrl: e.target.value }))}
                  />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label>Lineup members</Label>
                {(form.lineupMembers ?? []).map((m, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <Input
                      value={m.name}
                      onChange={(e) => updateLineupMember(i, { name: e.target.value })}
                      placeholder="Name"
                      className="flex-1"
                    />
                    <Input
                      value={m.role}
                      onChange={(e) => updateLineupMember(i, { role: e.target.value })}
                      placeholder="Role"
                      className="flex-1"
                    />
                    <label className="flex items-center gap-1 whitespace-nowrap text-xs">
                      <Checkbox
                        checked={m.isLeader}
                        onCheckedChange={(c) => updateLineupMember(i, { isLeader: c === true })}
                      />
                      Leader
                    </label>
                    <Button type="button" size="icon-sm" variant="ghost" onClick={() => removeLineupMember(i)}>
                      <Trash2 className="size-3.5" />
                    </Button>
                  </div>
                ))}
                <Button type="button" size="sm" variant="outline" onClick={addLineupMember}>
                  Add member
                </Button>
              </div>
            </div>
          ) : null}

          {form.profileType === "VENUE" ? (
            <div className="space-y-3 rounded-lg border border-border p-3">
              <p className="text-xs font-medium text-muted-foreground">Venue profile</p>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="user-venue-name">Venue name</Label>
                  <Input
                    id="user-venue-name"
                    value={form.venueName ?? ""}
                    onChange={(e) => setForm((f) => ({ ...f, venueName: e.target.value }))}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="user-capacity">Capacity</Label>
                  <Input
                    id="user-capacity"
                    type="number"
                    min={0}
                    value={form.capacity ?? ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, capacity: e.target.value === "" ? undefined : Number(e.target.value) }))
                    }
                  />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-venue-features">Features (comma-separated)</Label>
                <Input
                  id="user-venue-features"
                  value={csvText.venueFeatures}
                  onChange={(e) => setCsvText((c) => ({ ...c, venueFeatures: e.target.value }))}
                  placeholder="PA System, Green Room"
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="user-event-calendar">Event calendar (comma-separated links/IDs)</Label>
                <Input
                  id="user-event-calendar"
                  value={csvText.eventCalendar}
                  onChange={(e) => setCsvText((c) => ({ ...c, eventCalendar: e.target.value }))}
                />
              </div>
            </div>
          ) : null}

          <div className="space-y-1.5">
            <Label htmlFor="user-gender">Gender</Label>
            <Input
              id="user-gender"
              value={form.gender ?? ""}
              onChange={(e) => setForm((f) => ({ ...f, gender: e.target.value }))}
            />
          </div>

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
          {form.profileType !== "BUSINESS" && form.profileType !== "VENUE"
            ? arrayField("instruments", "Instruments", instrumentOptions)
            : null}
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
