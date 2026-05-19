#!/usr/bin/env python3
"""Second pass: migrate remaining println string literals."""
import re
import os

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")
PROPS = os.path.join(os.path.dirname(__file__), "..", "src", "recources", "messages_en.properties")

def load_props():
    d = {}
    with open(PROPS, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or "=" not in line:
                continue
            k, v = line.split("=", 1)
            v = v.replace("\\n", "\n").replace("\\:", ":")
            d.setdefault(v, k)
    return d

def fix_file(path, value_to_key):
    text = open(path, encoding="utf-8").read()
    if "LanguageManager" not in text and "println(\"" in text:
        # add import after package
        text = re.sub(
            r"(package [\w.]+;)\n",
            r"\1\n\nimport kz.synapse.utils.LanguageManager;\n",
            text,
            count=1,
        )
    changed = False

    def repl(m):
        nonlocal changed
        lit = m.group(1).replace("\\n", "\n")
        key = value_to_key.get(lit)
        if not key:
            return m.group(0)
        changed = True
        return f'System.out.println(LanguageManager.get("{key}"))'

    text2 = re.sub(
        r'System\.out\.println\s*\(\s*"((?:[^"\\]|\\.)*)"\s*\)',
        repl,
        text,
    )
    if changed:
        open(path, "w", encoding="utf-8", newline="\n").write(text2)
        print("Updated", path)

def main():
    v2k = load_props()
    for dirpath, _, files in os.walk(ROOT):
        for fn in files:
            if fn.endswith(".java"):
                fix_file(os.path.join(dirpath, fn), v2k)

if __name__ == "__main__":
    main()
