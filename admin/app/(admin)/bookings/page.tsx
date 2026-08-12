"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Plus } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { StatusBadge } from "@/components/shared/status-badge";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { studioBookingsRepo } from "@/services/repositories";
import type { StudioBookingDoc, WithId } from "@/types/firestore";
import { BookingEditorSheet } from "./booking-editor-sheet";

type Row = WithId<StudioBookingDoc>;

/** Next allowed status transitions per current status — keeps the action buttons contextual instead of showing every possible status always. */
const NEXT_STATUSES: Record<string, { label: string; next: string; destructive?: boolean }[]> = {
  requested: [
    { label: "Confirm", next: "confirmed" },
    { label: "Cancel", next: "cancelled", destructive: true },
  ],
  confirmed: [
    { label: "Mark Completed", next: "completed" },
    { label: "Cancel", next: "cancelled", destructive: true },
  ],
  payment_pending: [
    { label: "Confirm", next: "confirmed" },
    { label: "Cancel", next: "cancelled", destructive: true },
  ],
};

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.studioId", header: "Studio" },
  { accessorKey: "data.userId", header: "Booked by" },
  { accessorKey: "data.date", header: "Date" },
  { accessorKey: "data.slotText", header: "Slot" },
  {
    accessorKey: "data.sessionType",
    header: "Session",
    cell: ({ row }) => row.original.data.sessionType ?? <span className="text-muted-foreground">—</span>,
  },
  {
    accessorKey: "data.totalAmount",
    header: "Amount",
    cell: ({ row }) => `₹${(row.original.data.totalAmount ?? 0).toLocaleString("en-IN")}`,
  },
  {
    accessorKey: "data.bookingStatus",
    header: "Status",
    cell: ({ row }) =>
      row.original.data.bookingStatus ? (
        <StatusBadge status={row.original.data.bookingStatus} />
      ) : (
        <Badge variant="outline">unset</Badge>
      ),
  },
  {
    accessorKey: "data.paymentStatus",
    header: "Payment",
    cell: ({ row }) =>
      row.original.data.paymentStatus ? (
        <StatusBadge status={row.original.data.paymentStatus} />
      ) : (
        <span className="text-xs text-muted-foreground">—</span>
      ),
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const booking = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      const transitions = NEXT_STATUSES[booking.data.bookingStatus ?? "requested"] ?? [];
      if (!transitions.length) return null;
      return (
        <div className="flex justify-end gap-1" data-no-row-click>
          {transitions.map((t) => (
            <ConfirmDialog
              key={t.next}
              trigger={
                <Button size="sm" variant={t.destructive ? "destructive" : "outline"}>
                  {t.label}
                </Button>
              }
              title={`${t.label} this booking?`}
              description={`Sets bookingStatus to "${t.next}".`}
              destructive={t.destructive}
              onConfirm={async () => {
                await studioBookingsRepo.update(booking.id, { bookingStatus: t.next });
                toast.success(`Booking marked "${t.next}"`);
                refetch?.();
              }}
            />
          ))}
        </div>
      );
    },
  },
];

export default function BookingsPage() {
  const { hasAccess } = useRequireModule("bookings");
  const { data, loading, error, refetch } = useCollection(studioBookingsRepo, { orderByField: "createdAt", orderDirection: "desc" });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Bookings"
      description="Studio slot reservations across the platform. Status changes here are moderator-only — a booker can only ever cancel their own booking (see firestore.rules). Click a row to view/edit the full session configuration and payment breakdown."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New booking
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search bookings..."
        emptyTitle="No bookings yet"
        emptyDescription="Studio reservations will show up here."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <BookingEditorSheet booking={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
