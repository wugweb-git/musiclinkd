import os
import re

source_file = "app/src/main/java/com/example/onboarding/ui/screens/DiscoverScreens.kt"
out_dir = "app/src/main/java/com/example/onboarding/ui/screens/"

with open(source_file, "r") as f:
    lines = f.readlines()

header_lines = []
content_lines = []
in_header = True
for line in lines:
    if line.startswith("@Composable") or line.startswith("val ") or line.startswith("private val "):
        in_header = False
    
    if in_header:
        header_lines.append(line)
    else:
        content_lines.append(line)

content = "".join(content_lines)

# Split by top-level annotations or val
blocks = re.split(r'\n(?=@Composable\nfun |\n@OptIn|\nprivate val |\nval )', "\n" + content)
header = "".join(header_lines)

for i, block in enumerate(blocks):
    if not block.strip():
        continue
    
    match = re.search(r'fun\s+([A-Za-z0-9_]+)\s*\(', block)
    if match:
        name = match.group(1)
        filename = os.path.join(out_dir, f"{name}.kt")
        print(f"Writing {name}.kt")
        with open(filename, "w") as f:
            f.write(header.strip() + "\n\n" + block.strip() + "\n")
    else:
        with open(os.path.join(out_dir, f"Constants_{i}.kt"), "w") as f:
            f.write(header.strip() + "\n\n" + block.strip() + "\n")
