"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { StatusBadge } from "@/components/shared/status-badge";
import { projectsRepo } from "@/services/repositories";
import type { ProjectDoc, WithId } from "@/types/firestore";

type Row = WithId<ProjectDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.title", header: "Project" },
  { accessorKey: "data.creatorUid", header: "Creator" },
  { accessorKey: "data.roleRequired", header: "Role needed" },
  { accessorKey: "data.budget", header: "Budget" },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
];

export default function ProjectsPage() {
  return (
    <CollectionListPage
      moduleKey="projects"
      title="Projects"
      description="Open collaboration calls and project opportunities."
      repo={projectsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search projects..."
      emptyTitle="No projects yet"
      emptyDescription="Open project calls will show up here."
    />
  );
}
