function toCsvValue(value: unknown): string {
  if (value === null || value === undefined) return "";
  const str = typeof value === "object" ? JSON.stringify(value) : String(value);
  return `"${str.replace(/"/g, '""')}"`;
}

export function downloadCsv(filename: string, rows: { id: string; data: object }[]) {
  if (rows.length === 0) return;
  const records = rows as { id: string; data: Record<string, unknown> }[];
  const fields = Object.keys(records[0].data);
  const header = ["id", ...fields].join(",");
  const lines = records.map((row) =>
    [toCsvValue(row.id), ...fields.map((f) => toCsvValue(row.data[f]))].join(","),
  );
  const csv = [header, ...lines].join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
