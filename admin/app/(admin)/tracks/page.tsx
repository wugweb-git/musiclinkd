"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { MockListPage } from "@/components/shared/mock-list-page";
import { MOCK_TRACKS } from "@/lib/mock-data";

type Row = (typeof MOCK_TRACKS)[number];

const columns: ColumnDef<Row>[] = [
  { accessorKey: "title", header: "Track" },
  { accessorKey: "owner", header: "Owner" },
  { accessorKey: "plays", header: "Plays" },
];

export default function TracksPage() {
  return (
    <MockListPage
      moduleKey="tracks"
      title="Tracks"
      description="Not a modeled concept in the current schema — not obviously distinct from marketplace_items (category STEMS) or feed_posts audio. Needs a product decision on what “tracks” actually means before this gets real data."
      data={MOCK_TRACKS}
      columns={columns}
      searchPlaceholder="Search tracks..."
    />
  );
}
