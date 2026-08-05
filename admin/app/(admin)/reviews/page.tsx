"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Star, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { reviewsRepo } from "@/services/repositories";
import type { ReviewDoc, WithId } from "@/types/firestore";

type Row = WithId<ReviewDoc>;

const columns: ColumnDef<Row>[] = [
  {
    accessorKey: "data.targetType",
    header: "Target",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.targetType}</Badge>,
  },
  { accessorKey: "data.targetId", header: "Target ID" },
  { accessorKey: "data.authorUid", header: "Author" },
  {
    accessorKey: "data.rating",
    header: "Rating",
    cell: ({ row }) => (
      <span className="flex items-center gap-1">
        <Star className="size-3.5 fill-primary text-primary" /> {row.original.data.rating}
      </span>
    ),
  },
  { accessorKey: "data.text", header: "Review" },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const review = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title="Remove this review?"
            description="Use for abusive or fake reviews flagged through moderation."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await reviewsRepo.remove(review.id);
              toast.success("Review removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function ReviewsPage() {
  return (
    <CollectionListPage
      moduleKey="reviews"
      title="Reviews"
      description="Reviews left on user and studio profiles."
      repo={reviewsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search reviews..."
      emptyTitle="No reviews yet"
      emptyDescription="Reviews will show up here."
    />
  );
}
