"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Plus } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { NON_TOGGLEABLE_MODULES } from "@/config/constants";
import { useAuth } from "@/hooks/use-auth";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { modulesRepo } from "@/services/repositories";
import type { ModuleRegistryDoc, WithId } from "@/types/firestore";
import { ModuleEditorSheet } from "./module-editor-sheet";

type Row = WithId<ModuleRegistryDoc>;

export default function ModulesPage() {
  const { hasAccess } = useRequireModule("modules");
  const { user } = useAuth();
  const { data, loading, error, refetch } = useCollection(modulesRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);
  const [toggling, setToggling] = useState<string | null>(null);

  if (!hasAccess) return null;

  async function handleToggle(row: Row) {
    if (!user) return;
    setToggling(row.id);
    try {
      // v1: direct Firestore write, isAdmin()-gated by rules — no server
      // function required (Cloud Functions need Blaze; this project stays
      // on Spark). Doesn't mirror to Remote Config yet — that's a deferred
      // follow-up, see Edge_Functions_And_Errors.md.
      await modulesRepo.update(row.id, {
        enabled: !row.data.enabled,
        updatedBy: user.uid,
        updatedAt: modulesRepo.serverTimestamp() as never,
      });
      toast.success(`${row.data.label} ${!row.data.enabled ? "enabled" : "disabled"}`);
      refetch();
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to toggle module.";
      toast.error(message);
    } finally {
      setToggling(null);
    }
  }

  const columns: ColumnDef<Row>[] = [
    { accessorKey: "id", header: "Key" },
    { accessorKey: "data.label", header: "Label" },
    { accessorKey: "data.navModuleKey", header: "Admin nav item", cell: ({ row }) => row.original.data.navModuleKey ?? "—" },
    { accessorKey: "data.remoteConfigKey", header: "Remote Config key", cell: ({ row }) => row.original.data.remoteConfigKey ?? "—" },
    {
      accessorKey: "data.enabled",
      header: "Status",
      cell: ({ row }) =>
        row.original.data.enabled ? (
          <Badge variant="secondary">Enabled</Badge>
        ) : (
          <Badge variant="outline">Disabled</Badge>
        ),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => {
        const mod = row.original;
        const locked = !!mod.data.navModuleKey && NON_TOGGLEABLE_MODULES.includes(mod.data.navModuleKey as never);
        return (
          <div className="flex justify-end" data-no-row-click>
            {locked ? (
              <span className="text-xs text-muted-foreground">Core, can&apos;t be disabled</span>
            ) : (
              <ConfirmDialog
                trigger={
                  <Button size="sm" variant={mod.data.enabled ? "destructive" : "outline"} disabled={toggling === mod.id}>
                    {toggling === mod.id ? "Working…" : mod.data.enabled ? "Disable" : "Enable"}
                  </Button>
                }
                title={`${mod.data.enabled ? "Disable" : "Enable"} ${mod.data.label}?`}
                description={
                  mod.data.enabled
                    ? "Locks this module's admin route and nav item immediately for every admin. Doesn't affect the Android app in this pass."
                    : "Unlocks this module's admin route and nav item immediately."
                }
                destructive={mod.data.enabled}
                onConfirm={() => handleToggle(mod)}
              />
            )}
          </div>
        );
      },
    },
  ];

  return (
    <PageShell
      title="Modules"
      description="Enable/disable admin modules. Toggling writes directly to Firestore (isAdmin()-gated). Doesn't mirror to Remote Config yet — that's a deferred follow-up once a Firebase service-account credential is provisioned for it."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New module
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search modules..."
        emptyTitle="No modules registered yet"
        emptyDescription="Seed the registry with entries for marketplace, bookings, analytics, tracks, ai, business."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <ModuleEditorSheet module={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
