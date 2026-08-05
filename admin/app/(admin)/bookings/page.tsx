"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { studioBookingsRepo } from "@/services/repositories";
import type { StudioBookingDoc, WithId } from "@/types/firestore";

type Row = WithId<StudioBookingDoc>;

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
];

export default function BookingsPage() {
  return (
    <CollectionListPage
      moduleKey="bookings"
      title="Bookings"
      description="Studio slot reservations across the platform."
      repo={studioBookingsRepo}
      columns={columns}
      searchPlaceholder="Search bookings..."
      emptyTitle="No bookings yet"
      emptyDescription="Studio reservations will show up here."
    />
  );
}
