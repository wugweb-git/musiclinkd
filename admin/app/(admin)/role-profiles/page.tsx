"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Plus, Trash2 } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { roleProfilesRepo } from "@/services/repositories";
import type { RoleProfileDoc, WithId } from "@/types/firestore";
import { RoleProfileEditorSheet } from "./role-profile-editor-sheet";

type Row = WithId<RoleProfileDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.ownerUid", header: "Owner" },
  {
    accessorKey: "data.roleKey",
    header: "Role",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.roleKey}</Badge>,
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const profile = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title="Remove this role profile?"
            description="Removes this role's data permanently — the user's core profile and other role profiles are unaffected."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await roleProfilesRepo.remove(profile.id);
              toast.success("Role profile removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function RoleProfilesPage() {
  const { hasAccess } = useRequireModule("role-profiles");
  const { data, loading, error, refetch } = useCollection(roleProfilesRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Role Profiles"
      description="Per-role profile data (Singer, Musician, Lyricist, Music Director, Composer, Sound Engineer, DJ, RJ, Podcaster) — one document per role a user plays, kept separate from the core Users record so a multi-role person isn't bloating a single document. No confirmed app-side writer yet — see the plan's Part U."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New role profile
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search role profiles..."
        emptyTitle="No role profiles yet"
        emptyDescription="Role-specific profile data will show up here once wired up."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <RoleProfileEditorSheet profile={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
