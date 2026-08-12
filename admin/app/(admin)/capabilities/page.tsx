"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Check, Pencil, Plus, Trash2, X } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { capabilitiesRepo } from "@/services/repositories";
import type { CapabilityMatrixDoc, WithId } from "@/types/firestore";
import { CapabilityEditorSheet } from "./capability-editor-sheet";

type Row = WithId<CapabilityMatrixDoc>;

function boolCell(value: boolean) {
  return value ? <Check className="size-4 text-primary" /> : <X className="size-4 text-muted-foreground" />;
}

function buildColumns(onEdit: (row: Row) => void): ColumnDef<Row>[] {
  return [
    { accessorKey: "data.label", header: "Role" },
    { id: "profile", header: "Create Profile", cell: ({ row }) => boolCell(row.original.data.canCreateProfile) },
    { id: "opportunities", header: "Post Opportunities", cell: ({ row }) => boolCell(row.original.data.canPostOpportunities) },
    { id: "sell", header: "Sell Equipment", cell: ({ row }) => boolCell(row.original.data.canSellEquipment) },
    { id: "events", header: "Create Events", cell: ({ row }) => boolCell(row.original.data.canCreateEvents) },
    {
      id: "crm",
      header: "CRM Access",
      cell: ({ row }) => <Badge variant="outline">{row.original.data.crmAccess}</Badge>,
    },
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const cap = row.original;
        const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end gap-1" data-no-row-click>
            <Button size="icon-sm" variant="outline" onClick={() => onEdit(cap)}>
              <Pencil className="size-3.5" />
            </Button>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title={`Delete "${cap.data.label}"?`}
              description="Removes this role's capability entry."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await capabilitiesRepo.remove(cap.id);
                toast.success("Capability deleted");
                refetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];
}

export default function CapabilitiesPage() {
  const { hasAccess } = useRequireModule("capabilities");
  const { data, loading, error, refetch } = useCollection(capabilitiesRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  const columns = buildColumns((row) => {
    setSelected(row);
    setEditorOpen(true);
  });

  return (
    <PageShell
      title="Capabilities"
      description="What each role/profile type is permitted to do — public-read so the app can gate UI client-side, admin-write only. roleKey spans profileType values (talent) and businessType sub-types (studio/label/agency) plus admin, matching how this was originally specified rather than a strict profileType/businessType split."
      actions={
        <div className="flex gap-2">
          <Button
            size="sm"
            onClick={() => {
              setSelected(null);
              setEditorOpen(true);
            }}
          >
            <Plus className="size-4" /> New role
          </Button>
        </div>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search roles..."
        emptyTitle="No roles yet"
        emptyDescription="Add Talent/Studio/Label/Agency/Admin roles, or seed them via npm run seed."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <CapabilityEditorSheet capability={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
