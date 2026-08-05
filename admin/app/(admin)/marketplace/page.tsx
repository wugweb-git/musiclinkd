"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { marketplaceItemsRepo } from "@/services/repositories";
import type { MarketplaceItemDoc, WithId } from "@/types/firestore";

type Row = WithId<MarketplaceItemDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.title", header: "Item" },
  {
    accessorKey: "data.category",
    header: "Category",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.category}</Badge>,
  },
  { accessorKey: "data.condition", header: "Condition" },
  { accessorKey: "data.price", header: "Price" },
  { accessorKey: "data.sellerUid", header: "Seller" },
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
            title={`Remove "${item.data.title}"?`}
            description="Delists this marketplace listing permanently."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await marketplaceItemsRepo.remove(item.id);
              toast.success("Listing removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function MarketplacePage() {
  return (
    <CollectionListPage
      moduleKey="marketplace"
      title="Marketplace"
      description="Gear, STEM packs, and services listed for sale."
      repo={marketplaceItemsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search listings..."
      emptyTitle="No listings yet"
      emptyDescription="Marketplace items will show up here."
    />
  );
}
