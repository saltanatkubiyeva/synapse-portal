#!/usr/bin/env python3
"""Rebuild properties from keys referenced in Java."""
import re, os, glob

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")
PROPS_DIR = os.path.join(os.path.dirname(__file__), "..", "src", "recources")
MAP = os.path.join(os.path.dirname(__file__), "literal_map.txt")

# Curated EN (from UIStrings + dynamic); extends literal_map
CURATED_EN = {
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
    "login.title": "  LOGIN",
    "setup.firstLaunch": "  First launch detected. Creating default admin...",
    "setup.defaultAdmin": "  Default admin created: admin@uni.kz / admin",
    "setup.addUsersHint": "  Login and add other users manually.\n",
    "db.loaded": "Database loaded.",
    "db.startingFresh": "Starting fresh: {0}",
    "ui.separator": "\u2500" * 60,
    "msg.success.prefix": "\u2713 ",
    "msg.error.prefix": "\u2717 ",
    "common.cancel": "  0. Cancel",
    "common.back": "  0. Back",
    "common.skip": "  0. Skip",
    "common.cancelled": "Cancelled.",
    "common.none": "    None",
    "common.noStudents": "No students.",
    "common.periodPrompt": "  Period: 1.ATT1  2.ATT2",
    "common.schoolPrompt": "  School: 1.SITE  2.BS  3.ISE  4.KMA  5.OAG  6.SGE  7.SAM  8.SCHE",
    "common.approveRejectCancel": "  1. Approve  2. Reject  0. Cancel",
    "common.sendMessage.title": "  SEND MESSAGE",
    "common.sendTechRequest.title": "  SEND TECH REQUEST",
    "common.inbox.title": "  INBOX",
    "common.inbox.empty": "  No new messages.",
    "common.notifications.title": "  NOTIFICATIONS",
    "common.notifications.empty": "  No new notifications.",
    "common.newsFeed.title": "  NEWS FEED",
    "common.semesterPrompt": "  Semester: 1.FALL  2.SPRING  3.SUMMER",
    "teacher.students.in": "  STUDENTS IN: {0}",
    "admin.tempPassword": "Temporary password for {0}: {1}",
    "journal.noRecords": "No records for {0}",
    "journal.header": "Journal: {0} | {1}",
    "research.papersOf": "--- Papers of {0} ---",
    "journal.separator.short": "\u2500" * 50,
    "journal.col.date": "Date",
    "journal.col.lesson": "Lesson",
    "journal.col.score": "Score",
    "journal.col.comment": "Comment",
    "db.startingFresh": "Starting fresh: {0}",
    "student.availableCourses.title": "  AVAILABLE COURSES — {0}",
    "student.register.course": "\n  Course: {0}",
    "student.register.credits": "  Credits: {0}",
    "student.slots.for": "  SLOTS FOR: {0}",
    "student.slots.progress": "  Progress: Lectures {0}/{1}  |  Practices {2}/{3}",
    "dean.complaints.title": "  COMPLAINTS — {0}",
    "dean.complaints.students": "  Students: {0}",
    "schoolmanager.offerings.title": "  COURSE OFFERINGS — {0}",
    "schoolmanager.students.title": "  STUDENTS — {0}",
    "schoolmanager.teachers.title": "  TEACHERS — {0}",
    "schoolmanager.noGraduates": "  No graduate students in {0}",
    "schoolmanager.requirements": "  Requirements: {0} lecture(s) + {1} practice(s) per week",
    "schoolmanager.currentSlots": "  Current slots: {0}",
    "schoolmanager.accessibleSlots": "  Accessible slots: {0}",
    "coordinator.project.topic": "  PROJECT: {0}",
    "coordinator.project.participants": "  Participants ({0}):",
    "coordinator.project.papers": "  Published Papers ({0}):",
    "coordinator.topCited.title": "  TOP CITED RESEARCHER — {0}",
    "coordinator.supervisor.for": "  ASSIGN SUPERVISOR FOR: {0}",
    "ormanager.header": "  Semester: {0}  |  Registration: {1}",
    "ormanager.orgPending": "  ⚠ Org proposals pending: {0}",
    "ormanager.org.name": "  ORGANIZATION: {0}",
    "ormanager.org.description": "  Description: {0}",
    "ormanager.org.head": "  Head: {0}",
    "ormanager.org.members": "  Members ({0}):",
    "ormanager.org.pendingJoins": "  Pending join requests: {0}",
    "ormanager.proposals.review": "  1. Review Organization Proposals ({0} pending)",
    "ormanager.dissolve.warning": "  WARNING: This will dissolve ''{0}'' and remove all {1} members.",
    "student.org.name": "  ORG: {0}",
    "student.org.description": "  Description: {0}",
    "student.org.membersHead": "  Members: {0} | Head: {1}",
    "student.org.members.title": "  MEMBERS: {0}",
    "student.org.joinRequests.title": "  JOIN REQUESTS: {0}",
    "student.org.transfer.title": "  TRANSFER LEADERSHIP: {0}",
    "student.org.leave.confirm": "  Are you sure you want to leave '{0}'? (yes/no)",
    "student.ta.assisting": "  Assisting: {0}",
    "student.ta.course": "  Course:    {0} [{1}]",
    "researcher.hindex": "  Your h-index: {0}",
    "techsupport.newRequests": "  ⚠ NEW REQUESTS: {0}",
    "techsupport.requestStatus": "  {0} REQUEST",
    "teacher.menu.researcher": "  11. ★ Researcher Menu",
    "app.banner.top": "  \u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557",
    "app.banner.empty": "  \u2551                                                          \u2551",
    "app.banner.title": "  \u2551                      S Y N A P S E                       \u2551",
    "app.banner.subtitle": "  \u2551               University Management System               \u2551",
    "app.banner.bottom": "  \u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d",
}

def load_map():
    m = dict(CURATED_EN)
    if os.path.exists(MAP):
        with open(MAP, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or "\t" not in line:
                    continue
                val, key = line.split("\t", 1)
                m[key] = eval(val)
    return m

def slug_to_text(slug_parts):
    out = []
    for p in slug_parts:
        if p.isdigit():
            out.append(p + ".")
        elif len(p) <= 4 and p.isupper():
            out.append(p)
        else:
            out.append(p.replace("_", " ").capitalize())
    s = " ".join(out)
    if s and s[0].isdigit():
        return "  " + s
    return s

def key_to_en(key, known):
    if key in known:
        return known[key]
    parts = key.split(".")
    if "menus" in parts:
        i = parts.index("menus") + 2
        return slug_to_text(parts[i:])
    if parts[-1] in ("title", "none", "empty"):
        body = slug_to_text(parts[-2:-1] or parts[-2:])
        return ("  " + body.upper()) if parts[-1] == "title" else body
    return slug_to_text(parts[-3:])

def collect_keys():
    keys = set()
    for path in glob.glob(ROOT + "/**/*.java", recursive=True):
        text = open(path, encoding="utf-8").read()
        keys.update(re.findall(r'LanguageManager\.get\("([^"]+)"', text))
        keys.update(re.findall(r'UIStrings\.get\("([^"]+)"', text))
    return sorted(keys)

def esc(s):
    return s.replace("\\", "\\\\").replace("\n", "\\n").replace(":", "\\:")

# key -> (ru, kz) overrides (from former UIStrings)
RU_KZ = {
    "app.title": ("SYNAPSE Система управления университетом", "SYNAPSE Университет басқару жүйесі"),
    "menu.login": ("1. Войти", "1. Кіру"),
    "msg.welcome": ("Добро пожаловать, ", "Қош келдіңіз, "),
    "msg.goodbye": ("До свидания!", "Сау болыңыз!"),
    "msg.invalid": ("Неверный ввод. Попробуйте снова.", "Қате енгізу. Қайталап көріңіз."),
    "msg.empty": ("Записи не найдены.", "Жазбалар табылмады."),
    "lang.changed": ("Язык изменён на Русский.", "Тіл қазақшаға өзгертілді."),
}

def main():
    known = load_map()
    keys = collect_keys()
    en = {k: key_to_en(k, known) for k in keys}
    ru = {k: (RU_KZ.get(k, (en[k], en[k]))[0]) for k in keys}
    kz = {k: (RU_KZ.get(k, (en[k], en[k]))[1]) for k in keys}
    for lang, data, fname in [(en, en, "messages_en.properties"), (ru, ru, "messages_ru.properties"), (kz, kz, "messages_kz.properties")]:
        path = os.path.join(PROPS_DIR, fname)
        lines = [f"{k}={esc(data[k])}" for k in sorted(data)]
        open(path, "w", encoding="utf-8").write("\n".join(lines) + "\n")
    print(f"Rebuilt {len(keys)} keys")

if __name__ == "__main__":
    main()
