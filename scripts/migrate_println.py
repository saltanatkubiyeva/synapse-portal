#!/usr/bin/env python3
"""Replace hardcoded System.out.println string literals with LanguageManager.getString."""
import re
import os

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")
PROPS = os.path.join(os.path.dirname(__file__), "..", "src", "recources", "messages_en.properties")

def load_props():
    d = {}
    with open(PROPS, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            k, v = line.split("=", 1)
            d[v.replace("\\n", "\n")] = k
    return d

def main():
    value_to_key = load_props()
    for dirpath, _, files in os.walk(ROOT):
        for fn in files:
            if not fn.endswith(".java"):
                continue
            path = os.path.join(dirpath, fn)
            if "LanguageManager.java" in path or "UIStrings.java" in path:
                continue
            text = open(path, encoding="utf-8").read()
            orig = text
            if "import kz.synapse.utils.LanguageManager;" not in text:
                text = text.replace(
                    "package kz.synapse",
                    "package kz.synapse\n\nimport kz.synapse.utils.LanguageManager;",
                    1,
                ) if "package kz.synapse" in text else text
                # fix duplicate - only add if package exists
            changed = False

            def repl(m):
                nonlocal changed
                lit = bytes(m.group(1), "utf-8").decode("unicode_escape")
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
                if "import kz.synapse.utils.LanguageManager;" not in text2:
                    pkg_end = text2.find(";") + 1
                    text2 = (
                        text2[:pkg_end]
                        + "\n\nimport kz.synapse.utils.LanguageManager;"
                        + text2[pkg_end:]
                    )
                open(path, "w", encoding="utf-8", newline="\n").write(text2)
                print("Updated", path)

if __name__ == "__main__":
    main()
