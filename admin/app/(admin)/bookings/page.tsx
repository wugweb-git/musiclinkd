"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { StatusBadge } from "@/components/shared/status-badge";
import { studioBookingsRepo } from "@/services/repositories";
import type { StudioBookingDoc, WithId } from "@/types/firestore";

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
    accessorKey: "data.totalAmount",
    header: "Amount",
    cell: ({ row }) => `$${row.original.data.totalAmount?.toFixed(2) ?? "0.00"}`,
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
  return (
    <CollectionListPage
      moduleKey="bookings"
      title="Bookings"
      description="Studio slot reservations across the platform. Status changes here are moderator-only — a booker can only ever cancel their own booking (see firestore.rules)."
      repo={studioBookingsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search bookings..."
      emptyTitle="No bookings yet"
      emptyDescription="Studio reservations will show up here."
    />
  );
}
