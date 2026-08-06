"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Trash2, Music } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { marketplaceItemsRepo } from "@/services/repositories";
import type { MarketplaceItemDoc, WithId } from "@/types/firestore";

type Row = WithId<MarketplaceItemDoc>;

const columns: ColumnDef<Row>[] = [
  {
    accessorKey: "data.title",
    header: "Stem / Track",
    cell: ({ row }) => (
      <div className="flex items-center gap-2 font-medium">
        <Music className="size-4 text-purple-500 shrink-0" />
        <span>{row.original.data.title}</span>
      </div>
    ),
  },
  {
    accessorKey: "data.category",
    header: "Type",
    cell: ({ row }) => (
      <Badge variant="secondary" className="bg-purple-500/10 text-purple-600 dark:text-purple-400">
        {row.original.data.category}
      </Badge>
    ),
  },
  { accessorKey: "data.condition", header: "Format / Condition" },
  { accessorKey: "data.price", header: "Price" },
  { accessorKey: "data.sellerUid", header: "Artist / Seller" },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const item = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title={`Delete track "${item.data.title}"?`}
            description="Removes this stem listing permanently from the platform."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await marketplaceItemsRepo.remove(item.id);
              toast.success("Stem track removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function TracksPage() {
  return (
    <CollectionListPage
      moduleKey="tracks"
      title="Audio Stems & Tracks"
      description="Live audio stem packs, instrumental tracks, and sample packs listed on MusicLinkd."
      repo={marketplaceItemsRepo}
      listOptions={{
        whereClauses: [["category", "==", "STEMS"]],
      }}
      columns={columns}
      searchPlaceholder="Search stems & audio tracks..."
      emptyTitle="No stem tracks listed yet"
      emptyDescription="Audio stems and music sample packs listed by artists will appear here."
    />
  );
}

