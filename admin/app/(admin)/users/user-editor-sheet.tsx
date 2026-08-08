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
import { usersRepo } from "@/services/repositories";
import type { UserDoc, UserRole, WithId } from "@/types/firestore";

interface UserEditorSheetProps {
  /** Existing user to edit, or null to create a new one. */
  user: WithId<UserDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const ROLES: UserRole[] = ["ARTIST", "PRODUCER", "VOCALIST", "STUDIO", "LABEL"];

function emptyForm(): UserDoc {
  return {
    displayName: "",
    email: "",
    role: "ARTIST",
    isVerified: false,
    isBanned: false,
    isAvailable: true,
    hourlyRate: "",
    location: "",
    bio: "",
    genres: [],
    avatarUrl: "",
    createdAt: usersRepo.serverTimestamp() as never,
  };
}

export function UserEditorSheet({ user, open, onOpenChange, onSaved }: UserEditorSheetProps) {
  const [uid, setUid] = useState("");
  const [form, setForm] = useState<UserDoc>(emptyForm());
  const [genresText, setGenresText] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (user) {
      setUid(user.id);
      setForm(user.data);
      setGenresText(user.data.genres?.join(", ") ?? "");
    } else {
      setUid(crypto.randomUUID());
      setForm(emptyForm());
      setGenresText("");
    }
  }, [user, open]);

  async function handleSave() {
    if (!form.displayName || !form.email) return;
    setSaving(true);
    try {
      const genres = genresText
        .split(",")
        .map((g) => g.trim())
        .filter(Boolean);
      await usersRepo.create(uid, { ...form, genres });
      toast.success(user ? "User updated" : "User created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save user");
    } finally {
      setSaving(false);
    }
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

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="user-role">Role</Label>
              <Select value={form.role} onValueChange={(v) => setForm((f) => ({ ...f, role: v as UserRole }))}>
                <SelectTrigger id="user-role" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {ROLES.map((r) => (
                    <SelectItem key={r} value={r}>
                      {r}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="user-location">Location</Label>
              <Input
                id="user-location"
                value={form.location}
                onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))}
              />
            </div>
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

          <div className="space-y-1.5">
            <Label htmlFor="user-genres">Genres (comma-separated)</Label>
            <Input id="user-genres" value={genresText} onChange={(e) => setGenresText(e.target.value)} placeholder="Hindi Folk, Pop Vocals" />
          </div>

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
          <Button onClick={handleSave} disabled={saving || !form.displayName || !form.email}>
            {saving ? "Saving…" : user ? "Save changes" : "Create user"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
