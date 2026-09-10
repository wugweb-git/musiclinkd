import { Skeleton } from "@/components/ui/skeleton";
import { Table, TableBody, TableCell, TableRow } from "@/components/ui/table";

/**
 * Renders inside the same rounded-lg border container and the same
 * Table/TableRow/TableCell markup the real DataTable uses, so the row
 * height and borders match exactly — the previous version was a generic
 * bordered box with its own ad hoc row height, which caused a visible
 * layout jump the instant real data replaced it.
 */
export function TableSkeleton({ columns, rows = 8 }: { columns: number; rows?: number }) {
  return (
    <div className="overflow-hidden rounded-lg border">
      <Table>
        <TableBody>
          {Array.from({ length: rows }).map((_, rowIndex) => (
            // biome-ignore lint: static skeleton rows, no stable id available
            <TableRow key={rowIndex} className="hover:bg-transparent">
              {Array.from({ length: columns }).map((_, colIndex) => (
                // biome-ignore lint: static skeleton cells, no stable id available
                <TableCell key={colIndex}>
                  <Skeleton
                    className="h-4"
                    style={{ width: `${45 + (((rowIndex * 7 + colIndex * 13) % 40) - 20)}%` }}
                  />
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
