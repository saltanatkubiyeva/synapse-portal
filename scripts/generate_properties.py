#!/usr/bin/env python3
"""Generate messages_*.properties with semantic keys from Java println literals."""
import os
import re

OUT = os.path.join(os.path.dirname(__file__), "..", "src", "recources")
ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")

# key -> (en, ru, kz)
T = {}

def add(key, en, ru=None, kz=None):
    T[key] = (en, ru or en, kz or en)

# --- core / UIStrings ---
add("app.title", "SYNAPSE University Management System",
    "SYNAPSE Система управления университетом", "SYNAPSE Университет басқару жүйесі")
add("app.subtitle", "Kazakh-British Technical University",
    "Казахско-Британский Технический Университет", "Қазақ-Британ Техникалық Университеті")
add("menu.login", "1. Login", "1. Войти", "1. Кіру")
add("menu.language", "2. Language Settings", "2. Настройки языка", "2. Тіл баптаулары")
add("menu.exit", "3. Exit", "3. Выход", "3. Шығу")
add("prompt.email", "Email: ", "Email: ", "Email: ")
add("prompt.password", "Password: ", "Пароль: ", "Құпия сөз: ")
add("prompt.choice", "Your choice: ", "Ваш выбор: ", "Таңдауыңыз: ")
add("prompt.enter", "\nPress Enter to continue...", "\nНажмите Enter для продолжения...", "\nЖалғастыру үшін Enter басыңыз...")
add("msg.welcome", "Welcome, ", "Добро пожаловать, ", "Қош келдіңіз, ")
add("msg.goodbye", "Goodbye! See you soon.", "До свидания!", "Сау болыңыз!")
add("msg.invalid", "Invalid input. Please try again.", "Неверный ввод. Попробуйте снова.", "Қате енгізу. Қайталап көріңіз.")
add("msg.success", "Operation completed successfully.", "Операция выполнена успешно.", "Операция сәтті орындалды.")
add("msg.error", "Error: ", "Ошибка: ", "Қате: ")
add("msg.empty", "No records found.", "Записи не найдены.", "Жазбалар табылмады.")
add("msg.back", "0. Back", "0. Назад", "0. Артқа")
add("msg.logout", "0. Logout", "0. Выйти", "0. Шығу")
add("menu.admin", "=== ADMIN MENU ===", "=== МЕНЮ АДМИНИСТРАТОРА ===", "=== ӘКІМШІ МӘЗІРІ ===")
add("menu.teacher", "=== TEACHER MENU ===", "=== МЕНЮ ПРЕПОДАВАТЕЛЯ ===", "=== ОҚЫТУШЫ МӘЗІРІ ===")
add("menu.student", "=== STUDENT MENU ===", "=== МЕНЮ СТУДЕНТА ===", "=== СТУДЕНТ МӘЗІРІ ===")
add("menu.ormanager", "=== OR MANAGER MENU ===", "=== МЕНЮ ОР МЕНЕДЖЕРА ===", "=== ОР МЕНЕДЖЕР МӘЗІРІ ===")
add("menu.schoolmanager", "=== SCHOOL MANAGER MENU ===", "=== МЕНЮ ШКОЛЬНОГО МЕНЕДЖЕРА ===", "=== МЕКТЕП МЕНЕДЖЕРІ МӘЗІРІ ===")
add("menu.techsupport", "=== TECH SUPPORT MENU ===", "=== МЕНЮ ТЕХ. ПОДДЕРЖКИ ===", "=== ТЕХ. ҚОЛДАУ МӘЗІРІ ===")
add("menu.researcher", "=== RESEARCHER MENU ===", "=== МЕНЮ ИССЛЕДОВАТЕЛЯ ===", "=== ЗЕРТТЕУШІ МӘЗІРІ ===")
add("menu.dean", "=== DEAN MENU ===", "=== МЕНЮ ДЕКАНА ===", "=== ДЕКАН МӘЗІРІ ===")
add("menu.coordinator", "=== RESEARCH COORDINATOR MENU ===", "=== МЕНЮ КООРДИНАТОРА ===", "=== КООРДИНАТОР МӘЗІРІ ===")
add("lang.select", "Select language / Выберите язык / Тілді таңдаңыз:",
    "Select language / Выберите язык / Тілді таңдаңыз:",
    "Select language / Выберите язык / Тілді таңдаңыз:")
add("lang.en", "1. English", "1. English", "1. English")
add("lang.ru", "2. Русский", "2. Русский", "2. Русский")
add("lang.kz", "3. Қазақша", "3. Қазақша", "3. Қазақша")
add("lang.changed", "Language changed to English.", "Язык изменён на Русский.", "Тіл қазақшаға өзгертілді.")
add("lang.fileNotFound", "Language file not found", "Файл языка не найден", "Тіл файлы табылмады")
add("lang.loadError", "Error loading language file", "Ошибка загрузки файла языка", "Тіл файлын жүктеу қатесі")

add("login.title", "  LOGIN", "  ВХОД", "  КІРУ")
add("setup.firstLaunch", "  First launch detected. Creating default admin...",
    "  Первый запуск. Создание администратора по умолчанию...",
    "  Бірінші іске қосу. Әдепкі әкімші жасалуда...")
add("setup.defaultAdmin", "  Default admin created: admin@uni.kz / admin",
    "  Администратор создан: admin@uni.kz / admin",
    "  Әкімші жасалды: admin@uni.kz / admin")
add("setup.addUsersHint", "  Login and add other users manually.\n",
    "  Войдите и добавьте пользователей вручную.\n",
    "  Кіріп, пайдаланушыларды қолмен қосыңыз.\n")

add("db.loaded", "Database loaded.", "База данных загружена.", "Дерекқор жүктелді.")
add("db.startingFresh", "Starting fresh: {0}", "Новая база: {0}", "Жаңа дерекқор: {0}")

add("ui.separator", "\u2500" * 60)
add("msg.success.prefix", "✓ ")
add("msg.error.prefix", "✗ ")

add("common.cancel", "  0. Cancel", "  0. Отмена", "  0. Болдырмау")
add("common.back", "  0. Back", "  0. Назад", "  0. Артқа")
add("common.skip", "  0. Skip", "  0. Пропустить", "  0. Өткізу")
add("common.cancelled", "Cancelled.", "Отменено.", "Болдырылмады.")
add("common.none", "    None", "    Нет", "    Жоқ")
add("common.noStudents", "No students.", "Нет студентов.", "Студенттер жоқ.")
add("common.periodPrompt", "  Period: 1.ATT1  2.ATT2", "  Период: 1.ATT1  2.ATT2", "  Кезең: 1.ATT1  2.ATT2")
add("common.schoolPrompt", "  School: 1.SITE  2.BS  3.ISE  4.KMA  5.OAG  6.SGE  7.SAM  8.SCHE",
    "  Школа: 1.SITE  2.BS  3.ISE  4.KMA  5.OAG  6.SGE  7.SAM  8.SCHE",
    "  Мектеп: 1.SITE  2.BS  3.ISE  4.KMA  5.OAG  6.SGE  7.SAM  8.SCHE")
add("common.approveRejectCancel", "  1. Approve  2. Reject  0. Cancel",
    "  1. Одобрить  2. Отклонить  0. Отмена", "  1. Мақұлдау  2. Қабылдамау  0. Болдырмау")
add("common.sendMessage.title", "  SEND MESSAGE", "  ОТПРАВИТЬ СООБЩЕНИЕ", "  ХАБАРЛАМА ЖІБЕРУ")
add("common.sendTechRequest.title", "  SEND TECH REQUEST", "  ТЕХ. ЗАПРОС", "  ТЕХ. СҰРАУ")
add("common.inbox.title", "  INBOX", "  ВХОДЯЩИЕ", "  КІРУ ХАТТАРЫ")
add("common.inbox.empty", "  No new messages.", "  Нет новых сообщений.", "  Жаңа хабарламалар жоқ.")
add("common.notifications.title", "  NOTIFICATIONS", "  УВЕДОМЛЕНИЯ", "  ХАБАРЛАНДЫРУЛАР")
add("common.notifications.empty", "  No new notifications.", "  Нет новых уведомлений.", "  Жаңа хабарландырулар жоқ.")
add("common.newsFeed.title", "  NEWS FEED", "  НОВОСТИ", "  ЖАҢАЛЫҚТАР")
add("common.semesterPrompt", "  Semester: 1.FALL  2.SPRING  3.SUMMER",
    "  Семестр: 1.FALL  2.SPRING  3.SUMMER", "  Семестр: 1.FALL  2.SPRING  3.SUMMER")

# Dynamic / parameterized messages
add("teacher.students.in", "  STUDENTS IN: {0}", "  СТУДЕНТЫ В: {0}", "  СТУДЕНТТЕР: {0}")
add("teacher.menu.researcher", "  11. ★ Researcher Menu", "  11. ★ Меню исследователя", "  11. ★ Зерттеуші мәзірі")
add("student.header.suffix", "  |  Credits: {0}/21  |  GPA: {1}", "  |  Кредиты: {0}/21  |  GPA: {1}", "  |  Кредиттер: {0}/21  |  GPA: {1}")
add("student.availableCourses.title", "  AVAILABLE COURSES — {0}", "  ДОСТУПНЫЕ КУРСЫ — {0}", "  ҚОЛЖЕТІМДІ ПАНДАР — {0}")
add("student.register.course", "\n  Course: {0}", "\n  Курс: {0}", "\n  Пән: {0}")
add("student.register.credits", "  Credits: {0}", "  Кредиты: {0}", "  Кредиттер: {0}")
add("student.slots.for", "  SLOTS FOR: {0}", "  СЛОТЫ ДЛЯ: {0}", "  СЛОТТАР: {0}")
add("student.slots.progress", "  Progress: Lectures {0}/{1}  |  Practices {2}/{3}",
    "  Прогресс: Лекции {0}/{1}  |  Практики {2}/{3}", "  Прогресс: Дәрістер {0}/{1}  |  Практикалар {2}/{3}")
add("student.org.leave.confirm", "  Are you sure you want to leave ''{0}''? (yes/no)",
    "  Вы уверены, что хотите покинуть ''{0}''? (yes/no)", "  ''{0}'' ұйымынан шығасыз ба? (yes/no)")
add("student.ta.assisting", "  Assisting: {0}", "  Помощник: {0}", "  Көмекші: {0}")
add("student.ta.course", "  Course:    {0} [{1}]", "  Курс:    {0} [{1}]", "  Пән:    {0} [{1}]")
add("dean.complaints.title", "  COMPLAINTS — {0}", "  ЖАЛОБЫ — {0}", "  ШАҒЫМДАР — {0}")
add("dean.complaints.students", "  Students: {0}", "  Студенты: {0}", "  Студенттер: {0}")
add("schoolmanager.offerings.title", "  COURSE OFFERINGS — {0}", "  ПРЕДЛОЖЕНИЯ КУРСОВ — {0}", "  ПАНА ҰСЫНЫСТАРЫ — {0}")
add("schoolmanager.students.title", "  STUDENTS — {0}", "  СТУДЕНТЫ — {0}", "  СТУДЕНТТЕР — {0}")
add("schoolmanager.teachers.title", "  TEACHERS — {0}", "  ПРЕПОДАВАТЕЛИ — {0}", "  ОҚЫТУШЫЛАР — {0}")
add("schoolmanager.noGraduates", "  No graduate students in {0}", "  Нет аспирантов в {0}", "  {0} мектебінде аспирант жоқ")
add("schoolmanager.requirements", "  Requirements: {0} lectures + {1} practices/week",
    "  Требования: {0} лекций + {1} практик/нед", "  Талап: {0} дәріс + {1} практика/апта")
add("schoolmanager.currentSlots", "  Current slots: {0}", "  Текущие слоты: {0}", "  Ағымдағы слоттар: {0}")
add("schoolmanager.accessibleSlots", "  Accessible slots: {0}", "  Доступные слоты: {0}", "  Қолжетімді слоттар: {0}")
add("coordinator.project.topic", "  PROJECT: {0}", "  ПРОЕКТ: {0}", "  ЖОБА: {0}")
add("coordinator.project.participants", "  Participants ({0}):", "  Участники ({0}):", "  Қатысушылар ({0}):")
add("coordinator.project.papers", "  Published Papers ({0}):", "  Опубликованные статьи ({0}):", "  Жарияланған мақалалар ({0}):")
add("coordinator.topCited.title", "  TOP CITED RESEARCHER — {0}", "  ТОП ПО ЦИТИРОВАНИЯМ — {0}", "  ЕҢ КӨП ЦИТАТАЛАНҒАН — {0}")
add("coordinator.supervisor.for", "  ASSIGN SUPERVISOR FOR: {0}", "  НАЗНАЧИТЬ РУКОВОДИТЕЛЯ: {0}", "  ЖЕТЕКШІНІ ТАҒАЙЫНДАУ: {0}")
add("ormanager.header", "  Semester: {0}  |  Registration: {1}", "  Семестр: {0}  |  Регистрация: {1}", "  Семестр: {0}  |  Тіркелу: {1}")
add("ormanager.orgPending", "  ⚠ Org proposals pending: {0}", "  ⚠ Заявки организаций: {0}", "  ⚠ Ұйым ұсыныстары: {0}")
add("ormanager.org.name", "  ORGANIZATION: {0}", "  ОРГАНИЗАЦИЯ: {0}", "  ҰЙЫМ: {0}")
add("ormanager.org.description", "  Description: {0}", "  Описание: {0}", "  Сипаттама: {0}")
add("ormanager.org.head", "  Head: {0}", "  Руководитель: {0}", "  Басшы: {0}")
add("ormanager.org.members", "  Members ({0}):", "  Участники ({0}):", "  Мүшелер ({0}):")
add("ormanager.org.pendingJoins", "  Pending join requests: {0}", "  Заявки на вступление: {0}", "  Қосылу өтінімдері: {0}")
add("ormanager.proposals.review", "  1. Review Organization Proposals ({0} pending)",
    "  1. Рассмотреть заявки ({0} ожидают)", "  1. Ұсыныстарды қарау ({0} күтуде)")
add("ormanager.dissolve.warning", "  WARNING: This will dissolve ''{0}'' and remove all {1} members.",
    "  ВНИМАНИЕ: ''{0}'' будет распущена, удалено {1} участников.", "  ЕСКЕРТУ: ''{0}'' таратылады, {1} мүше алынады.")
add("student.org.name", "  ORG: {0}", "  ОРГ: {0}", "  ҰЙЫМ: {0}")
add("student.org.description", "  Description: {0}", "  Описание: {0}", "  Сипаттама: {0}")
add("student.org.membersHead", "  Members: {0} | Head: {1}", "  Участники: {0} | Руководитель: {1}", "  Мүшелер: {0} | Басшы: {1}")
add("student.org.members.title", "  MEMBERS: {0}", "  УЧАСТНИКИ: {0}", "  МҮШЕЛЕР: {0}")
add("student.org.joinRequests.title", "  JOIN REQUESTS: {0}", "  ЗАЯВКИ НА ВСТУПЛЕНИЕ: {0}", "  ҚОСЫЛУ ӨТІНІМДЕРІ: {0}")
add("student.org.transfer.title", "  TRANSFER LEADERSHIP: {0}", "  ПЕРЕДАТЬ РУКОВОДСТВО: {0}", "  БАСШЫЛЫҚТЫ БЕРУ: {0}")
add("researcher.hindex", "  Your h-index: {0}", "  Ваш h-index: {0}", "  Сіздің h-index: {0}")
add("techsupport.newRequests", "  ⚠ NEW REQUESTS: {0}", "  ⚠ НОВЫЕ ЗАПРОСЫ: {0}", "  ⚠ ЖАҢА СҰРАУЛАР: {0}")
add("techsupport.requestStatus", "  {0} REQUEST", "  ЗАПРОС: {0}", "  СҰРАУ: {0}")
add("admin.tempPassword", "Temporary password for {0}: {1}", "Временный пароль для {0}: {1}", "{0} уақытша құпия сөзі: {1}")
add("journal.noRecords", "No records for {0}", "Нет записей для {0}", "{0} үшін жазба жоқ")
add("journal.header", "Journal: {0} | {1}", "Журнал: {0} | {1}", "Журнал: {0} | {1}")
add("research.papersOf", "--- Papers of {0} ---", "--- Статьи {0} ---", "--- {0} мақалалары ---")
add("journal.separator.short", "\u2500" * 50)
add("msg.welcome.suffix", "!")
add("login.welcome", "\n  {0}{1}{2}\n")

# Scan remaining literals and auto-key by prefix
LITERAL_KEY = {}
USED_KEYS = set(T.keys())

def scan_literals():
    for dirpath, _, files in os.walk(ROOT):
        for fn in files:
            if not fn.endswith(".java"):
                continue
            path = os.path.join(dirpath, fn)
            rel = os.path.relpath(path, ROOT).replace("\\", "/").replace(".java", "")
            text = open(path, encoding="utf-8").read()
            for m in re.finditer(r'System\.out\.println\s*\(\s*"((?:[^"\\]|\\.)*)"\s*\)', text):
                lit = m.group(1).replace("\\n", "\n").replace("\\t", "\t")
                if "UIStrings.get" in lit:
                    continue
                if any(lit == t[0] for t in T.values()):
                    continue
                if lit in LITERAL_KEY:
                    continue
                slug = re.sub(r"[^a-zA-Z0-9]+", ".", lit.strip().lower())[:50].strip(".") or "line"
                base = f"{rel.replace('/', '.')}.{slug}"
                key = base
                n = 2
                while key in USED_KEYS:
                    key = f"{base}.{n}"
                    n += 1
                USED_KEYS.add(key)
                LITERAL_KEY[lit] = key

def register_scanned_literals():
    for lit, key in LITERAL_KEY.items():
        if key not in T:
            add(key, lit)

def esc(s):
    return s.replace("\\", "\\\\").replace("\n", "\\n").replace(":", "\\:")

def write(lang_idx, name):
    path = os.path.join(OUT, name)
    lines = []
    for key in sorted(T.keys()):
        lines.append(f"{key}={esc(T[key][lang_idx])}")
    open(path, "w", encoding="utf-8", newline="\n").write("\n".join(lines) + "\n")
    print(f"Wrote {len(T)} keys -> {path}")

if __name__ == "__main__":
    scan_literals()
    register_scanned_literals()
    print(f"Scanned {len(LITERAL_KEY)} unique println literals, {len(T)} total keys")
    write(0, "messages_en.properties")
    write(1, "messages_ru.properties")
    write(2, "messages_kz.properties")

    # value -> key map for migration
    m = {}
    for k, v in T.items():
        m[v[0]] = k
    map_path = os.path.join(os.path.dirname(__file__), "literal_map.txt")
    with open(map_path, "w", encoding="utf-8") as f:
        for val in sorted(m, key=len, reverse=True):
            f.write(f"{repr(val)}\t{m[val]}\n")
    print(f"Wrote literal map ({len(m)} entries)")
