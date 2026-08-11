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
import { StatusBadge } from "@/components/shared/status-badge";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { portfolioProjectsRepo } from "@/services/repositories";
import type { PortfolioProjectDoc, WithId } from "@/types/firestore";
import { PortfolioEditorSheet } from "./portfolio-editor-sheet";

type Row = WithId<PortfolioProjectDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.name", header: "Project" },
  { accessorKey: "data.ownerUid", header: "Owner" },
  {
    accessorKey: "data.type",
    header: "Type",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.type}</Badge>,
  },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
  {
    accessorKey: "data.visibility",
    header: "Visibility",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.visibility}</Badge>,
  },
  { accessorKey: "data.releaseDate", header: "Release date" },
  {
    id: "roles",
    header: "Owner roles",
    cell: ({ row }) => (row.original.data.ownerRoles ?? []).join(", ") || "—",
  },
  {
    id: "collaborators",
    header: "Collaborators",
    cell: ({ row }) => row.original.data.collaborators?.length ?? 0,
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const project = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title={`Remove "${project.data.name}"?`}
            description="Removes this portfolio project permanently — use for spam or abusive uploads."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await portfolioProjectsRepo.remove(project.id);
              toast.success("Project removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function PortfolioPage() {
  const { hasAccess } = useRequireModule("portfolio");
  const { data, loading, error, refetch } = useCollection(portfolioProjectsRepo, {
    orderByField: "createdAt",
    orderDirection: "desc",
  });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Portfolio"
      description="User-published portfolio projects (songs, albums, EPs, podcasts, DJ mixes, film scores, jingles, live performances, music videos). No confirmed app-side writer yet as of this session's audits — this module is ready to receive data the moment publishing is wired up. See admin/docs/architecture/Edge_Functions_And_Errors.md."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New project
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search portfolio projects..."
        emptyTitle="No portfolio projects yet"
        emptyDescription="Published projects will show up here once the app writes to portfolio_projects."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <PortfolioEditorSheet project={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}
