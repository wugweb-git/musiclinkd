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
import { useTaxonomyOptions } from "@/hooks/use-taxonomy-options";
import { studioBookingsRepo } from "@/services/repositories";
import type { StudioBookingDoc, WithId } from "@/types/firestore";

interface BookingEditorSheetProps {
  booking: WithId<StudioBookingDoc> | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSaved: () => void;
}

const BOOKING_STATUSES = ["requested", "confirmed", "payment_pending", "cancelled", "completed"] as const;
const PAYMENT_STATUSES = ["PENDING", "PAID", "REFUNDED", "FAILED"] as const;
const PAYMENT_METHODS = ["UPI", "Card", "Net Banking", "Wallet"] as const;

function emptyForm(): StudioBookingDoc {
  return {
    studioId: "",
    userId: "",
    date: "",
    slotText: "",
    totalAmount: 0,
    bookedHourSlots: [],
    bookingStatus: "requested",
    paymentStatus: "PENDING",
    createdAt: studioBookingsRepo.serverTimestamp() as never,
  };
}

export function BookingEditorSheet({ booking, open, onOpenChange, onSaved }: BookingEditorSheetProps) {
  const [id, setId] = useState("");
  const [form, setForm] = useState<StudioBookingDoc>(emptyForm());
  const [selectedEquipmentText, setSelectedEquipmentText] = useState("");
  const [addonsText, setAddonsText] = useState("");
  const [saving, setSaving] = useState(false);

  const sessionTypeOptions = useTaxonomyOptions("session_types");
  const durationTierOptions = useTaxonomyOptions("session_duration_tiers");

  useEffect(() => {
    if (booking) {
      setId(booking.id);
      setForm(booking.data);
      setSelectedEquipmentText((booking.data.selectedEquipment ?? []).join(", "));
      setAddonsText((booking.data.addons ?? []).join(", "));
    } else {
      setId(crypto.randomUUID());
      setForm(emptyForm());
      setSelectedEquipmentText("");
      setAddonsText("");
    }
  }, [booking, open]);

  function field<K extends keyof StudioBookingDoc>(key: K, value: StudioBookingDoc[K]) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  async function handleSave() {
    if (!form.studioId || !form.userId || !form.date) return;
    setSaving(true);
    try {
      const selectedEquipment = selectedEquipmentText
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);
      const addons = addonsText
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);
      await studioBookingsRepo.create(id, { ...form, selectedEquipment, addons });
      toast.success(booking ? "Booking updated" : "Booking created");
      onSaved();
      onOpenChange(false);
    } catch {
      toast.error("Failed to save booking");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full sm:max-w-lg">
        <SheetHeader>
          <SheetTitle>{booking ? "Edit booking" : "New booking"}</SheetTitle>
          <SheetDescription>{booking ? `Doc ID: ${id}` : "Manually create a studio booking record."}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-col gap-3 overflow-y-auto px-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="booking-studio">Studio ID</Label>
              <Input id="booking-studio" value={form.studioId} onChange={(e) => field("studioId", e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-user">Booked by (user ID)</Label>
              <Input id="booking-user" value={form.userId} onChange={(e) => field("userId", e.target.value)} />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="booking-date">Date</Label>
              <Input id="booking-date" type="date" value={form.date} onChange={(e) => field("date", e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-slot">Slot text</Label>
              <Input id="booking-slot" value={form.slotText} onChange={(e) => field("slotText", e.target.value)} placeholder="10:00 AM – 2:00 PM" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label htmlFor="booking-status">Booking status</Label>
              <Select value={form.bookingStatus ?? "requested"} onValueChange={(v) => field("bookingStatus", v ?? "requested")}>
                <SelectTrigger id="booking-status" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {BOOKING_STATUSES.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-payment-status">Payment status</Label>
              <Select value={form.paymentStatus ?? "PENDING"} onValueChange={(v) => field("paymentStatus", v as StudioBookingDoc["paymentStatus"])}>
                <SelectTrigger id="booking-payment-status" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {PAYMENT_STATUSES.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="rounded-md border p-3 space-y-3">
            <p className="text-xs font-medium text-muted-foreground">Configure Session</p>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="booking-session-type">Session type</Label>
                {sessionTypeOptions ? (
                  <Select value={form.sessionType ?? ""} onValueChange={(v) => field("sessionType", v ?? "")}>
                    <SelectTrigger id="booking-session-type" className="w-full">
                      <SelectValue placeholder="Select…" />
                    </SelectTrigger>
                    <SelectContent>
                      {sessionTypeOptions.map((o) => (
                        <SelectItem key={o.id} value={o.data.value}>
                          {o.data.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                ) : (
                  <Input id="booking-session-type" value={form.sessionType ?? ""} onChange={(e) => field("sessionType", e.target.value)} placeholder="recording" />
                )}
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="booking-duration-tier">Duration tier</Label>
                {durationTierOptions ? (
                  <Select value={form.durationTier ?? ""} onValueChange={(v) => field("durationTier", v ?? "")}>
                    <SelectTrigger id="booking-duration-tier" className="w-full">
                      <SelectValue placeholder="Select…" />
                    </SelectTrigger>
                    <SelectContent>
                      {durationTierOptions.map((o) => (
                        <SelectItem key={o.id} value={o.data.value}>
                          {o.data.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                ) : (
                  <Input id="booking-duration-tier" value={form.durationTier ?? ""} onChange={(e) => field("durationTier", e.target.value)} placeholder="half_day" />
                )}
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Checkbox id="booking-engineer" checked={form.engineerRequested ?? false} onCheckedChange={(v) => field("engineerRequested", v === true)} />
              <Label htmlFor="booking-engineer" className="font-normal">
                Engineer requested
              </Label>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-equipment">Selected equipment (comma-separated)</Label>
              <Input id="booking-equipment" value={selectedEquipmentText} onChange={(e) => setSelectedEquipmentText(e.target.value)} placeholder="SSL Duality Delta 48-Channel Console" />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-addons">
                Add-ons (comma-separated —{" "}
                <span className="text-muted-foreground">no master-data list yet, freeform</span>)
              </Label>
              <Input id="booking-addons" value={addonsText} onChange={(e) => setAddonsText(e.target.value)} placeholder="Parking, Valet, Catering" />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="booking-notes">Notes</Label>
              <Textarea id="booking-notes" value={form.notes ?? ""} onChange={(e) => field("notes", e.target.value)} rows={2} />
            </div>
          </div>

          <div className="rounded-md border p-3 space-y-3">
            <p className="text-xs font-medium text-muted-foreground">Payment</p>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="booking-total">Total amount (₹)</Label>
                <Input id="booking-total" type="number" min={0} value={form.totalAmount} onChange={(e) => field("totalAmount", e.target.valueAsNumber || 0)} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="booking-payment-method">Payment method</Label>
                <Select value={form.paymentMethod ?? ""} onValueChange={(v) => field("paymentMethod", v as StudioBookingDoc["paymentMethod"])}>
                  <SelectTrigger id="booking-payment-method" className="w-full">
                    <SelectValue placeholder="Select…" />
                  </SelectTrigger>
                  <SelectContent>
                    {PAYMENT_METHODS.map((m) => (
                      <SelectItem key={m} value={m}>
                        {m}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="grid grid-cols-3 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="booking-tax">Tax (₹)</Label>
                <Input id="booking-tax" type="number" min={0} value={form.taxAmount ?? 0} onChange={(e) => field("taxAmount", e.target.valueAsNumber || 0)} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="booking-deposit">Deposit (₹)</Label>
                <Input id="booking-deposit" type="number" min={0} value={form.depositAmount ?? 0} onChange={(e) => field("depositAmount", e.target.valueAsNumber || 0)} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="booking-discount">Discount (₹)</Label>
                <Input id="booking-discount" type="number" min={0} value={form.discountAmount ?? 0} onChange={(e) => field("discountAmount", e.target.valueAsNumber || 0)} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="booking-promo">Promo code</Label>
                <Input id="booking-promo" value={form.promoCode ?? ""} onChange={(e) => field("promoCode", e.target.value)} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="booking-txn">Payment transaction ID</Label>
                <Input id="booking-txn" value={form.paymentTransactionId ?? ""} onChange={(e) => field("paymentTransactionId", e.target.value)} />
              </div>
            </div>
          </div>
        </div>
        <SheetFooter>
          <Button onClick={handleSave} disabled={saving || !form.studioId || !form.userId || !form.date}>
            {saving ? "Saving…" : booking ? "Save changes" : "Create booking"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
