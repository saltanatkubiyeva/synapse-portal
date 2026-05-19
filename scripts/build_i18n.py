#!/usr/bin/env python3
"""Build messages_en.properties from UIStrings keys and scan println literals."""
import re
import os

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")

# UIStrings EN (source of truth for existing keys)
UI_EN = {
    "app.title": "SYNAPSE University Management System",
    "app.subtitle": "Kazakh-British Technical University",
    "menu.login": "1. Login",
    "menu.language": "2. Language Settings",
    "menu.exit": "3. Exit",
    "prompt.email": "Email: ",
    "prompt.password": "Password: ",
    "prompt.choice": "Your choice: ",
    "prompt.enter": "\nPress Enter to continue...",
    "msg.welcome": "Welcome, ",
    "msg.goodbye": "Goodbye! See you soon.",
    "msg.invalid": "Invalid input. Please try again.",
    "msg.success": "Operation completed successfully.",
    "msg.error": "Error: ",
    "msg.empty": "No records found.",
    "msg.back": "0. Back",
    "msg.logout": "0. Logout",
    "menu.admin": "=== ADMIN MENU ===",
    "menu.teacher": "=== TEACHER MENU ===",
    "menu.student": "=== STUDENT MENU ===",
    "menu.ormanager": "=== OR MANAGER MENU ===",
    "menu.schoolmanager": "=== SCHOOL MANAGER MENU ===",
    "menu.techsupport": "=== TECH SUPPORT MENU ===",
    "menu.researcher": "=== RESEARCHER MENU ===",
    "menu.dean": "=== DEAN MENU ===",
    "menu.coordinator": "=== RESEARCH COORDINATOR MENU ===",
    "lang.select": "Select language / Выберите язык / Тілді таңдаңыз:",
    "lang.en": "1. English",
    "lang.ru": "2. Русский",
    "lang.kz": "3. Қазақша",
    "lang.changed": "Language changed to English.",
}

def slug(s):
    s = re.sub(r"[^a-zA-Z0-9]+", ".", s.strip().lower())
    return re.sub(r"\.+", ".", s).strip(".")

def main():
    props = dict(UI_EN)
    for dirpath, _, files in os.walk(ROOT):
        for f in files:
            if not f.endswith(".java"):
                continue
            path = os.path.join(dirpath, f)
            rel = os.path.relpath(path, ROOT).replace("\\", "/")
            text = open(path, encoding="utf-8").read()
            for m in re.finditer(r'System\.out\.println\s*\(\s*"((?:[^"\\]|\\.)*)"\s*\)', text):
                lit = m.group(1).encode().decode("unicode_escape")
                if "UIStrings.get" in lit or lit in ("", "\n"):
                    continue
                key = f"auto.{slug(rel)}.{slug(lit)[:60]}"
                if lit not in [v for v in props.values()]:
                    props.setdefault(key, lit)
    out = os.path.join(os.path.dirname(__file__), "..", "src", "recources", "messages_en.properties")
    lines = []
    for k in sorted(props.keys()):
        v = props[k].replace("\n", "\\n")
        lines.append(f"{k}={v}")
    open(out, "w", encoding="utf-8").write("\n".join(lines) + "\n")
    print(f"Wrote {len(props)} keys to {out}")

if __name__ == "__main__":
    main()
