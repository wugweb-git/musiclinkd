"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { collectionGroup, getDocs } from "firebase/firestore";
import { LayoutList, Pencil, Plus, Rows3, Sparkles, Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { StatusBadge } from "@/components/shared/status-badge";
import { TruncatedCell } from "@/components/shared/truncated-cell";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { getFirebaseDb } from "@/services/firebase";
import { flowsRepo } from "@/services/repositories";
import type { FlowDoc, WithId } from "@/types/firestore";
import { FlowEditorSheet } from "./flow-editor-sheet";
import { DEFAULT_FLOWS } from "./default-flows";
import { FlowsStackedView } from "./flows-stacked-view";

type Row = WithId<FlowDoc>;

function buildColumns(onEdit: (row: Row) => void, screenCounts: Record<string, number>): ColumnDef<Row>[] {
  return [
    {
      id: "flow",
      header: "Flow",
      cell: ({ row }) => (
        <span>
          <span className="font-mono text-muted-foreground">Flow {row.original.data.order}</span> — {row.original.data.title}
        </span>
      ),
    },
    {
      id: "screens",
      header: "Screens",
      cell: ({ row }) => <Badge variant="outline">{screenCounts[row.original.id] ?? "…"}</Badge>,
    },
    {
      accessorKey: "data.status",
      header: "Status",
      cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
    },
    {
      accessorKey: "data.description",
      header: "Description",
      cell: ({ row }) => <TruncatedCell text={row.original.data.description} maxWidth={420} />,
    },
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
  const [viewMode, setViewMode] = useState<"flat" | "stacked">("flat");
  const [screenCounts, setScreenCounts] = useState<Record<string, number>>({});

  useEffect(() => {
    // Single collectionGroup query instead of one .count() per flow row —
    // firestore.rules' screens rule doesn't depend on {flowId}, so this is
    // valid under the same security rules. Was previously 23-25 parallel
    // aggregate queries, one per row, which is why "Screens" showed "…"
    // indefinitely on slower connections.
    let cancelled = false;
    getDocs(collectionGroup(getFirebaseDb(), "screens")).then((snap) => {
      if (cancelled) return;
      const counts: Record<string, number> = {};
      for (const d of snap.docs) {
        const flowId = d.ref.parent.parent?.id;
        if (!flowId) continue;
        counts[flowId] = (counts[flowId] ?? 0) + 1;
      }
      setScreenCounts(counts);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  if (!hasAccess) return null;

  const columns = buildColumns((row) => {
    setSelected(row);
    setEditorOpen(true);
  }, screenCounts);

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
          <div className="flex rounded-md border p-0.5">
            <Button
              size="icon-sm"
              variant={viewMode === "flat" ? "secondary" : "ghost"}
              onClick={() => setViewMode("flat")}
              title="Flat view"
            >
              <LayoutList className="size-3.5" />
            </Button>
            <Button
              size="icon-sm"
              variant={viewMode === "stacked" ? "secondary" : "ghost"}
              onClick={() => setViewMode("stacked")}
              title="Stacked view — flows with their screens nested underneath"
            >
              <Rows3 className="size-3.5" />
            </Button>
          </div>
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
      {viewMode === "flat" ? (
        <DataTable
          columns={columns}
          data={data}
          loading={loading}
          error={error}
          searchPlaceholder="Search flows..."
          emptyTitle="No flows yet"
          emptyDescription='Click "Seed defaults" to populate the 24-flow starter map, or add one manually.'
          onRowClick={(row) => router.push(`/flows/screens?id=${row.id}`)}
          meta={{ refetch }}
        />
      ) : (
        <FlowsStackedView flows={data} loading={loading} onFlowClick={(id) => router.push(`/flows/screens?id=${id}`)} />
      )}

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
