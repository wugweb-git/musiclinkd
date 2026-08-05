"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { BadgeCheck, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { studiosRepo } from "@/services/repositories";
import type { StudioDoc, WithId } from "@/types/firestore";

type Row = WithId<StudioDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.name", header: "Studio" },
  { accessorKey: "data.location", header: "Location" },
  { accessorKey: "data.hourlyRate", header: "Rate" },
  {
    accessorKey: "data.rating",
    header: "Rating",
    cell: ({ row }) => `${row.original.data.rating ?? 0} (${row.original.data.reviewCount ?? 0})`,
  },
  {
    accessorKey: "data.verifiedGold",
    header: "Verified",
    cell: ({ row }) => (row.original.data.verifiedGold ? <BadgeCheck className="size-4 text-primary" /> : "—"),
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const studio = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title={`Remove ${studio.data.name}?`}
            description="This delists the studio permanently."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await studiosRepo.remove(studio.id);
              toast.success("Studio removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function StudiosPage() {
  return (
    <CollectionListPage
      moduleKey="studios"
      title="Studios"
      description="Recording studio and rehearsal space listings."
      repo={studiosRepo}
      columns={columns}
      searchPlaceholder="Search studios..."
      emptyTitle="No studios yet"
      emptyDescription="Studio listings will show up here."
    />
  );
}
