"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Pencil, Plus, Sparkles, Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { flowsRepo } from "@/services/repositories";
import type { FlowDoc, FlowStatus, WithId } from "@/types/firestore";
import { FlowEditorSheet } from "./flow-editor-sheet";
import { DEFAULT_FLOWS } from "./default-flows";

type Row = WithId<FlowDoc>;

const STATUS_VARIANT: Record<FlowStatus, "default" | "secondary" | "destructive" | "outline"> = {
  documented: "default",
  partial: "secondary",
  "app-mocked": "outline",
  missing: "destructive",
};

function buildColumns(onEdit: (row: Row) => void): ColumnDef<Row>[] {
  return [
    { accessorKey: "data.order", header: "#" },
    { accessorKey: "data.title", header: "Flow" },
    {
      accessorKey: "data.status",
      header: "Status",
      cell: ({ row }) => <Badge variant={STATUS_VARIANT[row.original.data.status]}>{row.original.data.status}</Badge>,
    },
    { accessorKey: "data.description", header: "Description" },
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const flow = row.original;
        const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end gap-1" data-no-row-click>
            <Button size="icon-sm" variant="outline" onClick={() => onEdit(flow)}>
              <Pencil className="size-3.5" />
            </Button>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title={`Delete "${flow.data.title}"?`}
              description="This does not delete the screens underneath it — remove those first if you want a clean removal."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await flowsRepo.remove(flow.id);
                toast.success("Flow deleted");
                refetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];
}

export default function FlowsPage() {
  const { hasAccess } = useRequireModule("flows");
  const router = useRouter();
  const { data, loading, error, refetch } = useCollection(flowsRepo, { orderByField: "order", orderDirection: "asc" });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);
  const [seeding, setSeeding] = useState(false);

  if (!hasAccess) return null;

  const columns = buildColumns((row) => {
    setSelected(row);
    setEditorOpen(true);
  });

  async function handleSeed() {
    setSeeding(true);
    try {
      const now = flowsRepo.serverTimestamp();
      let created = 0;
      for (const f of DEFAULT_FLOWS) {
        const existing = await flowsRepo.get(f.key);
        if (existing) continue;
        await flowsRepo.create(f.key, { ...f, createdAt: now as never, updatedAt: now as never });
        created += 1;
      }
      toast.success(created ? `Seeded ${created} default flow(s)` : "All default flows already exist");
      refetch();
    } catch {
      toast.error("Failed to seed default flows");
    } finally {
      setSeeding(false);
    }
  }

  return (
    <PageShell
      title="Flows"
      description="The product's flow map, as data instead of only markdown docs. Flow is the authoritative unit — each flow's screens (drill in to a row) can carry multiple named states and the Firestore collections they actually touch."
      actions={
        <div className="flex gap-2">
          <Button size="sm" variant="outline" onClick={handleSeed} disabled={seeding}>
            <Sparkles className="size-4" /> {seeding ? "Seeding…" : "Seed defaults"}
          </Button>
          <Button
            size="sm"
            onClick={() => {
              setSelected(null);
              setEditorOpen(true);
            }}
          >
            <Plus className="size-4" /> New flow
          </Button>
        </div>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search flows..."
        emptyTitle="No flows yet"
        emptyDescription='Click "Seed defaults" to populate the 22-flow starter map, or add one manually.'
        onRowClick={(row) => router.push(`/flows/screens?id=${row.id}`)}
        meta={{ refetch }}
      />

      <FlowEditorSheet
        flow={selected}
        open={editorOpen}
        onOpenChange={setEditorOpen}
        onSaved={refetch}
        nextOrder={data.length}
      />
    </PageShell>
  );
}
