"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { StatusBadge } from "@/components/shared/status-badge";
import { verificationRequestsRepo } from "@/services/repositories";
import type { VerificationRequestDoc, WithId } from "@/types/firestore";

type Row = WithId<VerificationRequestDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.subjectType", header: "Type" },
  { accessorKey: "data.subjectId", header: "Subject ID" },
  { accessorKey: "data.submittedBy", header: "Submitted by" },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const req = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      if (req.data.status !== "PENDING") return null;
      return (
        <div className="flex justify-end gap-1" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="sm" variant="outline">
                Approve
              </Button>
            }
            title="Approve this verification request?"
            description={`Marks ${req.data.subjectType.toLowerCase()} ${req.data.subjectId} as verified.`}
            onConfirm={async () => {
              await verificationRequestsRepo.update(req.id, { status: "APPROVED" });
              toast.success("Verification approved");
              refetch?.();
            }}
          />
          <ConfirmDialog
            trigger={
              <Button size="sm" variant="destructive">
                Reject
              </Button>
            }
            title="Reject this verification request?"
            description="The submitter will need to resubmit documents."
            destructive
            onConfirm={async () => {
              await verificationRequestsRepo.update(req.id, { status: "REJECTED" });
              toast.success("Verification rejected");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function VerificationPage() {
  return (
    <CollectionListPage
      moduleKey="verification"
      title="Verification queue"
      description="User and studio verification requests awaiting review."
      repo={verificationRequestsRepo}
      columns={columns}
      listOptions={{ orderByField: "submittedAt", orderDirection: "desc" }}
      searchPlaceholder="Search requests..."
      emptyTitle="Queue is empty"
      emptyDescription="Nothing pending verification right now."
    />
  );
}
