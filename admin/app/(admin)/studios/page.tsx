"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { BadgeCheck, Plus, Trash2 } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { formatHourlyRate } from "@/lib/utils";
import { studiosRepo } from "@/services/repositories";
import type { StudioDoc, WithId } from "@/types/firestore";
import { StudioEditorSheet } from "./studio-editor-sheet";

type Row = WithId<StudioDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.name", header: "Studio" },
  { accessorKey: "data.location", header: "Location" },
  { accessorKey: "data.hourlyRate", header: "Rate", cell: ({ row }) => formatHourlyRate(row.original.data.hourlyRate) },
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
  const { hasAccess } = useRequireModule("studios");
  const { data, loading, error, refetch } = useCollection(studiosRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Studios"
      description="Recording studio and rehearsal space listings."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New studio
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search studios..."
        emptyTitle="No studios yet"
        emptyDescription="Studio listings will show up here."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <StudioEditorSheet studio={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
