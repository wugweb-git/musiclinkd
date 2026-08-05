"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { MockListPage } from "@/components/shared/mock-list-page";
import { MOCK_ANALYTICS_EVENTS } from "@/lib/mock-data";

type Row = (typeof MOCK_ANALYTICS_EVENTS)[number];

const columns: ColumnDef<Row>[] = [
  { accessorKey: "event", header: "Event" },
  { accessorKey: "count", header: "Count (30d)" },
  { accessorKey: "trend", header: "Trend" },
];

export default function AnalyticsPage() {
  return (
    <MockListPage
      moduleKey="analytics"
      title="Analytics"
      description="Firebase Analytics data isn't queryable ad hoc from client code the way Firestore is — a real dashboard here needs either a BigQuery export + aggregation backend, or a link out to the Firebase console. Both were out of scope for this pass."
      data={MOCK_ANALYTICS_EVENTS}
      columns={columns}
      searchPlaceholder="Search events..."
    />
  );
}
