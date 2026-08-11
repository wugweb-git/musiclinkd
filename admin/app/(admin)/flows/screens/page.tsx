"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { ArrowLeft, Plus, Sparkles, Trash2 } from "lucide-react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { flowScreensRepo } from "@/services/repositories/flow-screens-repository";
import type { FlowScreenDoc, WithId } from "@/types/firestore";
import { DEFAULT_SCREENS } from "./default-screens";
import { ScreenEditorSheet } from "./screen-editor-sheet";

type Row = WithId<FlowScreenDoc>;

function FlowScreensDetail() {
  const searchParams = useSearchParams();
  const flowId = searchParams.get("id") ?? "";
  const { hasAccess } = useRequireModule("flows");
  const repo = flowScreensRepo(flowId);
  const { data, loading, error, refetch } = useCollection(repo, { orderByField: "order", orderDirection: "asc" });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);
  const [seeding, setSeeding] = useState(false);
  const defaultScreens = DEFAULT_SCREENS[flowId];

  async function handleSeedScreens() {
    if (!defaultScreens) return;
    setSeeding(true);
    try {
      const now = repo.serverTimestamp();
      let created = 0;
      for (const s of defaultScreens) {
        const existing = await repo.get(s.key);
        if (existing) continue;
        await repo.create(s.key, { ...s, createdAt: now as never, updatedAt: now as never });
        created += 1;
      }
      toast.success(created ? `Seeded ${created} default screen(s)` : "All default screens already exist");
      refetch();
    } catch {
      toast.error("Failed to seed default screens");
    } finally {
      setSeeding(false);
    }
  }

  if (!hasAccess) return null;
  if (!flowId) {
    return (
      <PageShell title="Flows" description="No flow selected.">
        <Link href="/flows" className="text-sm text-primary underline">
          Back to flows
        </Link>
      </PageShell>
    );
  }

  const columns: ColumnDef<Row>[] = [
    { accessorKey: "data.order", header: "#" },
    { accessorKey: "data.title", header: "Screen" },
    {
      id: "states",
      header: "States",
      cell: ({ row }) => (
        <div className="flex flex-wrap gap-1">
          {(row.original.data.states ?? []).map((s) => (
            <Badge key={s} variant="outline">
              {s}
            </Badge>
          ))}
        </div>
      ),
    },
    {
      id: "collections",
      header: "Collections touched",
      cell: ({ row }) => (row.original.data.collectionsTouched ?? []).join(", ") || "—",
    },
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const screen = row.original;
        const tableRefetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end" data-no-row-click>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title={`Delete "${screen.data.title}"?`}
              description="Removes this screen from the flow map."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await repo.remove(screen.id);
                toast.success("Screen deleted");
                tableRefetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];

  return (
    <PageShell
      title={`Flow — ${flowId}`}
      description="Screens in this flow, in order, with the states each can render and the Firestore collections it reads or writes."
      actions={
        <div className="flex gap-2">
          {defaultScreens ? (
            <Button size="sm" variant="outline" onClick={handleSeedScreens} disabled={seeding}>
              <Sparkles className="size-4" /> {seeding ? "Seeding…" : "Seed default screens"}
            </Button>
          ) : null}
          <Button
            size="sm"
            onClick={() => {
              setSelected(null);
              setEditorOpen(true);
            }}
          >
            <Plus className="size-4" /> New screen
          </Button>
        </div>
      }
    >
      <Link href="/flows" className="inline-flex w-fit items-center gap-1 text-sm text-primary underline">
        <ArrowLeft className="size-3.5" /> Back to flows
      </Link>
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search screens..."
        emptyTitle="No screens yet"
        emptyDescription="Add the screens that make up this flow."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <ScreenEditorSheet
        flowId={flowId}
        screen={selected}
        open={editorOpen}
        onOpenChange={setEditorOpen}
        onSaved={refetch}
        nextOrder={data.length}
      />
    </PageShell>
  );
}

export default function FlowScreensPage() {
  return (
    <Suspense fallback={null}>
      <FlowScreensDetail />
    </Suspense>
  );
}
