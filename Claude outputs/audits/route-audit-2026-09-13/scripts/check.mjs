import { readFileSync } from "node:fs";
const lines = readFileSync(process.argv[2], "utf8").trim().split("\n").map((l) => l.split("|"));
for (let i = 0; i < lines.length; i += 8) {
  const batch = lines.slice(i, i + 8);
  const res = await Promise.all(batch.map(async ([p, u]) => {
    const r = await fetch("http://localhost:8080" + u);
    const h = await r.text();
    const title = /<title>([^<]*)/.exec(h)?.[1] ?? "";
    const body = h.replace(/<script[\s\S]*?<\/script>/g, "").replace(/<[^>]+>/g, " ");
    const nf = /not found|doesn.t exist|couldn.t find|no longer available|isn.t available|404/i.exec(body)?.[0] ?? "";
    const main = body.replace(/\s+/g, " ").trim().slice(0, 90);
    return `${r.status}\t${nf ? "NF:" + nf : "-"}\t${p}\t${u}\t${title}\t${main}`;
  }));
  console.log(res.join("\n"));
}
