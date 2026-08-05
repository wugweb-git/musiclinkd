"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { StatusBadge } from "@/components/shared/status-badge";
import { ANALYTICS_EVENTS } from "@/config/constants";
import { trackEvent } from "@/services/analytics";
import { moderationFlagsRepo } from "@/services/repositories";
import type { ModerationFlagDoc, WithId } from "@/types/firestore";

type Row = WithId<ModerationFlagDoc>;

const columns: ColumnDef<Row>[] = [
  {
    accessorKey: "data.targetType",
    header: "Target",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.targetType}</Badge>,
  },
  { accessorKey: "data.targetId", header: "Target ID" },
  { accessorKey: "data.reason", header: "Reason" },
  { accessorKey: "data.reportedBy", header: "Reported by" },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const flag = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      if (flag.data.status !== "PENDING") return null;
      return (
        <div className="flex justify-end gap-1" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="sm" variant="destructive">
                Action
              </Button>
            }
            title="Action this report?"
            description="Marks the flag as actioned — record the follow-up (removal, warning, etc.) separately."
            destructive
            onConfirm={async () => {
              await moderationFlagsRepo.update(flag.id, { status: "ACTIONED" });
              await trackEvent(ANALYTICS_EVENTS.moderationActioned, { targetType: flag.data.targetType });
              toast.success("Flag actioned");
              refetch?.();
            }}
          />
          <ConfirmDialog
            trigger={
              <Button size="sm" variant="outline">
                Dismiss
              </Button>
            }
            title="Dismiss this report?"
            description="No action needed — the content stays as-is."
            onConfirm={async () => {
              await moderationFlagsRepo.update(flag.id, { status: "DISMISSED" });
              toast.success("Flag dismissed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function ModerationPage() {
  return (
    <CollectionListPage
      moduleKey="moderation"
      title="Moderation"
      description="Reports against posts, audio, reviews, marketplace listings, and users."
      repo={moderationFlagsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search flags..."
      emptyTitle="Queue is empty"
      emptyDescription="No open reports right now."
    />
  );
}
