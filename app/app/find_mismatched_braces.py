def find_mismatch(filename):
    with open(filename, 'r') as f:
        lines = f.readlines()
    
    stack = []
    for i, line in enumerate(lines):
        line_num = i + 1
        # Strip comments and strings to avoid false positives
        clean_line = ""
        in_string = False
        escaped = False
        j = 0
        while j < len(line):
            char = line[j]
            if in_string:
                if char == '\\':
                    escaped = not escaped
                elif char == '"' and not escaped:
                    in_string = False
                else:
                    escaped = False
            else:
                if char == '"':
                    in_string = True
                elif char == '/' and j + 1 < len(line) and line[j+1] == '/':
                    break # ignore rest of line comment
                elif char == '{':
                    clean_line += '{'
                elif char == '}':
                    clean_line += '}'
            j += 1
            
        for char in clean_line:
            if char == '{':
                stack.append(line_num)
            elif char == '}':
                if not stack:
                    print(f"Excess '}}' at line {line_num}")
                else:
                    stack.pop()
                    
    if stack:
        print(f"Unclosed '{{' opened at lines:")
        for line in stack:
            print(f"  Line {line}: {lines[line-1].strip()}")

find_mismatch("app/src/main/java/com/example/onboarding/ui/screens/DiscoverScreens.kt")
