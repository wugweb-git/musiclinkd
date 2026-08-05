"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { MockListPage } from "@/components/shared/mock-list-page";
import { MOCK_MESSAGES } from "@/lib/mock-data";

type Row = (typeof MOCK_MESSAGES)[number];

const columns: ColumnDef<Row>[] = [
  { accessorKey: "participants", header: "Participants" },
  { accessorKey: "lastMessage", header: "Last message" },
  { accessorKey: "updatedAt", header: "Updated" },
];

export default function MessagesPage() {
  return (
    <MockListPage
      moduleKey="messages"
      title="Messages"
      description="Admins currently cannot read private user DMs — firestore.rules restricts /chat_threads to thread members only. Granting broader access is a privacy decision the approved plan flagged rather than made unilaterally."
      data={MOCK_MESSAGES}
      columns={columns}
      searchPlaceholder="Search threads..."
    />
  );
}
