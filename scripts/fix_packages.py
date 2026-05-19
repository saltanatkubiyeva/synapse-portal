#!/usr/bin/env python3
import re, os, glob

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")

for path in glob.glob(ROOT + "/**/*.java", recursive=True):
    text = open(path, encoding="utf-8").read()
    m = re.search(
        r"package kz\.synapse\s*\n\s*import kz\.synapse\.utils\.LanguageManager;\.([\w.]+);",
        text,
    )
    if not m:
        continue
    suffix = m.group(1)
    fixed = re.sub(
        r"package kz\.synapse\s*\n\s*import kz\.synapse\.utils\.LanguageManager;\." + re.escape(suffix) + r";",
        f"package kz.synapse.{suffix};\n\nimport kz.synapse.utils.LanguageManager;",
        text,
        count=1,
    )
    open(path, "w", encoding="utf-8", newline="\n").write(fixed)
    print("Fixed", path)
