"use client";

import {
  type ColumnDef,
  type ExpandedState,
  type GroupingState,
  type RowSelectionState,
  type SortingState,
  flexRender,
  getCoreRowModel,
  getExpandedRowModel,
  getFilteredRowModel,
  getGroupedRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";
import { ChevronDown, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-react";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { EmptyState } from "./empty-state";
import { ErrorState } from "./error-state";
import { TableSkeleton } from "./table-skeleton";

export interface DataTableMeta {
  /** Re-run the page's data fetch — call after a mutation in a cell renderer. */
  refetch?: () => void;
}

/** One option in the optional "Group by" dropdown — id must match a column's accessorKey/id. */
export interface GroupableColumn {
  id: string;
  label: string;
}

const DEFAULT_PAGE_SIZES = [10, 25, 50, 100];

interface DataTableProps<T> {
  columns: ColumnDef<T>[];
  data: T[];
  loading?: boolean;
  error?: string | null;
  searchPlaceholder?: string;
  emptyTitle?: string;
  emptyDescription?: string;
  bulkActions?: (selectedRows: T[]) => React.ReactNode;
  onRowClick?: (row: T) => void;
  meta?: DataTableMeta;
  /** Columns the admin can group rows by, shown as a "Group by" dropdown above the table. Omit to keep the flat list (existing behavior, unchanged). */
  groupableColumns?: GroupableColumn[];
  /** Page-size choices for the selector — defaults to [10, 25, 50, 100]. */
  pageSizeOptions?: number[];
}

export function DataTable<T>({
  columns,
  data,
  loading,
  error,
  searchPlaceholder = "Search...",
  emptyTitle = "No records",
  emptyDescription = "Nothing here yet.",
  bulkActions,
  onRowClick,
  meta,
  groupableColumns,
  pageSizeOptions = DEFAULT_PAGE_SIZES,
}: DataTableProps<T>) {
  const [globalFilter, setGlobalFilter] = useState("");
  const [sorting, setSorting] = useState<SortingState>([]);
  const [rowSelection, setRowSelection] = useState<RowSelectionState>({});
  const [grouping, setGrouping] = useState<GroupingState>([]);
  const [expanded, setExpanded] = useState<ExpandedState>(true);

  const table = useReactTable({
    data,
    columns,
    meta,
    state: { globalFilter, sorting, rowSelection, grouping, expanded },
    onGlobalFilterChange: setGlobalFilter,
    onSortingChange: setSorting,
    onRowSelectionChange: setRowSelection,
    onGroupingChange: setGrouping,
    onExpandedChange: setExpanded,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getGroupedRowModel: getGroupedRowModel(),
    getExpandedRowModel: getExpandedRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    initialState: { pagination: { pageSize: pageSizeOptions[0] ?? 10 } },
    autoResetExpanded: false,
  });

  const selectedRows = table.getSelectedRowModel().rows.map((r) => r.original);
  const groupedBy = grouping[0];

  // Re-expand every group whenever the grouping key changes (or grouping is
  // turned on) — otherwise switching "Group by" leaves the new groups
  // collapsed by default, which reads as "the data disappeared."
  useEffect(() => {
    if (grouping.length > 0) setExpanded(true);
  }, [grouping]);

  if (error) return <ErrorState message={error} />;

  return (
    <div className="space-y-3">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <Input
            placeholder={searchPlaceholder}
            value={globalFilter}
            onChange={(e) => setGlobalFilter(e.target.value)}
            className="max-w-sm"
          />
          {groupableColumns && groupableColumns.length > 0 ? (
            <Select
              value={groupedBy ?? "__none__"}
              onValueChange={(v) => setGrouping(!v || v === "__none__" ? [] : [v])}
            >
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Group by…" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__none__">No grouping</SelectItem>
                {groupableColumns.map((g) => (
                  <SelectItem key={g.id} value={g.id}>
                    Group by {g.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          ) : null}
        </div>
        {bulkActions && selectedRows.length > 0 ? bulkActions(selectedRows) : null}
      </div>

      {loading ? (
        <TableSkeleton columns={columns.length} />
      ) : data.length === 0 ? (
        <EmptyState title={emptyTitle} description={emptyDescription} />
      ) : (
        <div className="rounded-lg border">
          <Table>
            <TableHeader className="sticky top-0 z-10 bg-background">
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) => (
                    <TableHead
                      key={header.id}
                      className={header.column.getCanSort() ? "cursor-pointer select-none" : undefined}
                      onClick={header.column.getToggleSortingHandler()}
                    >
                      {flexRender(header.column.columnDef.header, header.getContext())}
                      {{ asc: " ▲", desc: " ▼" }[header.column.getIsSorted() as string] ?? ""}
                    </TableHead>
                  ))}
                </TableRow>
              ))}
            </TableHeader>
            <TableBody>
              {table.getRowModel().rows.map((row) => {
                if (row.getIsGrouped()) {
                  const cell = row.getVisibleCells().find((c) => c.column.id === row.groupingColumnId);
                  return (
                    <TableRow key={row.id} className="bg-muted/40 hover:bg-muted/60" onClick={row.getToggleExpandedHandler()}>
                      <TableCell colSpan={columns.length} className="cursor-pointer font-medium">
                        <span className="inline-flex items-center gap-1.5">
                          {row.getIsExpanded() ? <ChevronDown className="size-3.5" /> : <ChevronRight className="size-3.5" />}
                          {cell ? flexRender(cell.column.columnDef.cell, cell.getContext()) : String(row.groupingValue ?? "—")}
                          <span className="ml-1 text-xs font-normal text-muted-foreground">({row.subRows.length})</span>
                        </span>
                      </TableCell>
                    </TableRow>
                  );
                }
                return (
                  <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                    className={onRowClick ? "cursor-pointer" : undefined}
                    onClick={(e) => {
                      if ((e.target as HTMLElement).closest("[data-no-row-click]")) return;
                      onRowClick?.(row.original);
                    }}
                  >
                    {row.getVisibleCells().map((cell) =>
                      cell.getIsAggregated() || (cell.getIsGrouped() && !row.getIsGrouped()) ? null : (
                        <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                      ),
                    )}
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </div>
      )}

      {!loading && data.length > 0 ? (
        <div className="flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm text-muted-foreground">
            {selectedRows.length > 0
              ? `${selectedRows.length} of ${table.getFilteredRowModel().rows.length} selected`
              : (() => {
                  const total = table.getFilteredRowModel().rows.length;
                  const pageIndex = table.getState().pagination.pageIndex;
                  const pageSize = table.getState().pagination.pageSize;
                  const from = total === 0 ? 0 : pageIndex * pageSize + 1;
                  const to = Math.min(total, (pageIndex + 1) * pageSize);
                  return grouping.length > 0 ? `${total} record(s)` : `${from}–${to} of ${total}`;
                })()}
          </p>
          {grouping.length === 0 ? (
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-1.5">
                <span className="text-sm text-muted-foreground">Rows per page</span>
                <Select value={String(table.getState().pagination.pageSize)} onValueChange={(v) => table.setPageSize(Number(v))}>
                  <SelectTrigger className="w-[76px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {pageSizeOptions.map((size) => (
                      <SelectItem key={size} value={String(size)}>
                        {size}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <span className="text-sm text-muted-foreground">
                Page {table.getState().pagination.pageIndex + 1} of {Math.max(1, table.getPageCount())}
              </span>
              <div className="flex items-center gap-1">
                <Button variant="outline" size="icon-sm" onClick={() => table.setPageIndex(0)} disabled={!table.getCanPreviousPage()} aria-label="First page">
                  <ChevronsLeft className="size-3.5" />
                </Button>
                <Button variant="outline" size="sm" onClick={() => table.previousPage()} disabled={!table.getCanPreviousPage()}>
                  Previous
                </Button>
                <Button variant="outline" size="sm" onClick={() => table.nextPage()} disabled={!table.getCanNextPage()}>
                  Next
                </Button>
                <Button
                  variant="outline"
                  size="icon-sm"
                  onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                  disabled={!table.getCanNextPage()}
                  aria-label="Last page"
                >
                  <ChevronsRight className="size-3.5" />
                </Button>
              </div>
            </div>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
