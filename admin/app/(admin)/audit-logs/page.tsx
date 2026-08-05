"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { auditLogsRepo } from "@/services/repositories";
import type { AuditLogDoc, WithId } from "@/types/firestore";

type Row = WithId<AuditLogDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.actorUid", header: "Actor" },
  { accessorKey: "data.action", header: "Action" },
  { accessorKey: "data.targetPath", header: "Target" },
];

export default function AuditLogsPage() {
  return (
    <CollectionListPage
      moduleKey="audit-logs"
      title="Audit logs"
      description="Admin action trail. Client-writable for now — lock to server-only once a Cloud Functions backend exists, so the trail can't be edited by an admin covering their own tracks."
      repo={auditLogsRepo}
      columns={columns}
      listOptions={{ orderByField: "timestamp", orderDirection: "desc" }}
      searchPlaceholder="Search logs..."
      emptyTitle="No activity yet"
      emptyDescription="Admin actions will be recorded here."
    />
  );
}
