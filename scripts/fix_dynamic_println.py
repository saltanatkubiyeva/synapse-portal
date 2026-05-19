#!/usr/bin/env python3
"""Replace common dynamic println patterns with LanguageManager.get."""
import re, glob, os

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "kz", "synapse")

REPLACEMENTS = [
    (r'System\.out\.println\("  AVAILABLE COURSES — " \+ Database\.getInstance\(\)\.getCurrentSemester\(\)\)',
     'System.out.println(LanguageManager.get("student.availableCourses.title", Database.getInstance().getCurrentSemester()))'),
    (r'System\.out\.println\("\\n  Course: " \+ chosen\.getCourse\(\)\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.register.course", chosen.getCourse().getName()))'),
    (r'System\.out\.println\("  Credits: " \+ chosen\.getCourse\(\)\.getCredits\(\)\)',
     'System.out.println(LanguageManager.get("student.register.credits", chosen.getCourse().getCredits()))'),
    (r'System\.out\.println\("  SLOTS FOR: " \+ offering\.getCourse\(\)\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.slots.for", offering.getCourse().getName()))'),
    (r'System\.out\.println\("  Progress: Lectures " \+ lChosen \+ "/" \+ offering\.getCourse\(\)\.getLecturesPerWeek\(\)\s*\+ "  \|  Practices " \+ pChosen \+ "/" \+ offering\.getCourse\(\)\.getPracticesPerWeek\(\)\)',
     'System.out.println(LanguageManager.get("student.slots.progress", lChosen, offering.getCourse().getLecturesPerWeek(), pChosen, offering.getCourse().getPracticesPerWeek()))'),
    (r'System\.out\.println\("  COMPLAINTS — " \+ dean\.getSchool\(\)\)',
     'System.out.println(LanguageManager.get("dean.complaints.title", dean.getSchool()))'),
    (r'System\.out\.println\("  Students: " \+ c\.getAboutStudents\(\)\.stream\(\)',
     'System.out.println(LanguageManager.get("dean.complaints.students", c.getAboutStudents().stream()'),
    (r'System\.out\.println\("  COURSE OFFERINGS — " \+ manager\.getSchool\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.offerings.title", manager.getSchool()))'),
    (r'System\.out\.println\("  STUDENTS — " \+ manager\.getSchool\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.students.title", manager.getSchool()))'),
    (r'System\.out\.println\("  TEACHERS — " \+ manager\.getSchool\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.teachers.title", manager.getSchool()))'),
    (r'System\.out\.println\("  No graduate students in " \+ manager\.getSchool\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.noGraduates", manager.getSchool()))'),
    (r'System\.out\.println\("  Requirements: " \+ offering\.getCourse\(\)\.getLecturesPerWeek\(\)\s*\+ " lectures \+ " \+ offering\.getCourse\(\)\.getPracticesPerWeek\(\) \+ " practices/week"\)',
     'System.out.println(LanguageManager.get("schoolmanager.requirements", offering.getCourse().getLecturesPerWeek(), offering.getCourse().getPracticesPerWeek()))'),
    (r'System\.out\.println\("  Current slots: " \+ offering\.getSlots\(\)\.size\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.currentSlots", offering.getSlots().size()))'),
    (r'System\.out\.println\("  Accessible slots: " \+ ta\.getAccessibleSlots\(\)\.size\(\)\)',
     'System.out.println(LanguageManager.get("schoolmanager.accessibleSlots", ta.getAccessibleSlots().size()))'),
    (r'System\.out\.println\("  PROJECT: " \+ project\.getTopic\(\)\)',
     'System.out.println(LanguageManager.get("coordinator.project.topic", project.getTopic()))'),
    (r'System\.out\.println\("  Participants \(" \+ project\.getParticipants\(\)\.size\(\) \+ "\):"\)',
     'System.out.println(LanguageManager.get("coordinator.project.participants", project.getParticipants().size()))'),
    (r'System\.out\.println\("  Published Papers \(" \+ project\.getPublishedPapers\(\)\.size\(\) \+ "\):"\)',
     'System.out.println(LanguageManager.get("coordinator.project.papers", project.getPublishedPapers().size()))'),
    (r'System\.out\.println\("  TOP CITED RESEARCHER — " \+ label\)',
     'System.out.println(LanguageManager.get("coordinator.topCited.title", label))'),
    (r'System\.out\.println\("  ASSIGN SUPERVISOR FOR: " \+ gs\.getName\(\)\)',
     'System.out.println(LanguageManager.get("coordinator.supervisor.for", gs.getName()))'),
    (r'System\.out\.println\("  Semester: " \+ sem \+ "  \|  Registration: "\s*\+ \(Database\.getInstance\(\)\.isRegistrationOpen\(\) \? "OPEN" : "CLOSED"\)\)',
     'System.out.println(LanguageManager.get("ormanager.header", sem, Database.getInstance().isRegistrationOpen() ? "OPEN" : "CLOSED"))'),
    (r'if \(pendingOrgs > 0\) System\.out\.println\("  ⚠ Org proposals pending: " \+ pendingOrgs\)',
     'if (pendingOrgs > 0) System.out.println(LanguageManager.get("ormanager.orgPending", pendingOrgs))'),
    (r'System\.out\.println\("  ORGANIZATION: " \+ org\.getName\(\)\)',
     'System.out.println(LanguageManager.get("ormanager.org.name", org.getName()))'),
    (r'System\.out\.println\("  Description: " \+ org\.getDescription\(\)\)',
     'System.out.println(LanguageManager.get("ormanager.org.description", org.getDescription()))'),
    (r'System\.out\.println\("  Head: " \+ \(org\.getHead\(\) != null \? org\.getHead\(\)\.getName\(\) : "—"\)\)',
     'System.out.println(LanguageManager.get("ormanager.org.head", org.getHead() != null ? org.getHead().getName() : "—"))'),
    (r'System\.out\.println\("  Members \(" \+ org\.getMembers\(\)\.size\(\) \+ "\):"\)',
     'System.out.println(LanguageManager.get("ormanager.org.members", org.getMembers().size()))'),
    (r'System\.out\.println\("  Pending join requests: " \+ org\.getJoinRequests\(\)\.size\(\)\)',
     'System.out.println(LanguageManager.get("ormanager.org.pendingJoins", org.getJoinRequests().size()))'),
    (r'System\.out\.println\("  1\. Review Organization Proposals \(" \+ pending \+ " pending\)"\)',
     'System.out.println(LanguageManager.get("ormanager.proposals.review", pending))'),
    (r'System\.out\.println\("  ORG: " \+ org\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.org.name", org.getName()))'),
    (r'System\.out\.println\("  Description: " \+ org\.getDescription\(\)\)',
     'System.out.println(LanguageManager.get("student.org.description", org.getDescription()))'),
    (r'System\.out\.println\("  Members: " \+ org\.getMembers\(\)\.size\(\) \+ " \| Head: "\s*\+ \(org\.getHead\(\) != null \? org\.getHead\(\)\.getName\(\) : "—"\)\)',
     'System.out.println(LanguageManager.get("student.org.membersHead", org.getMembers().size(), org.getHead() != null ? org.getHead().getName() : "—"))'),
    (r'System\.out\.println\("  MEMBERS: " \+ org\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.org.members.title", org.getName()))'),
    (r'System\.out\.println\("  JOIN REQUESTS: " \+ org\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.org.joinRequests.title", org.getName()))'),
    (r'System\.out\.println\("  TRANSFER LEADERSHIP: " \+ org\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.org.transfer.title", org.getName()))'),
    (r'System\.out\.println\("  Are you sure you want to leave \'\" \+ org\.getName\(\) \+ "\'\\? \(yes/no\)"\)',
     'System.out.println(LanguageManager.get("student.org.leave.confirm", org.getName()))'),
    (r'System\.out\.println\("  Assisting: " \+ ta\.getAssistedTeacher\(\)\.getName\(\)\)',
     'System.out.println(LanguageManager.get("student.ta.assisting", ta.getAssistedTeacher().getName()))'),
    (r'System\.out\.println\("  Course:    " \+ ta\.getOffering\(\)\.getCourse\(\)\.getName\(\)\s*\+ " \[" \+ ta\.getOffering\(\)\.getSemester\(\) \+ "\]"\)',
     'System.out.println(LanguageManager.get("student.ta.course", ta.getOffering().getCourse().getName(), ta.getOffering().getSemester()))'),
    (r'System\.out\.println\("  Your h-index: " \+ researcher\.calculateHIndex\(\)\)',
     'System.out.println(LanguageManager.get("researcher.hindex", researcher.calculateHIndex()))'),
    (r'System\.out\.println\("  ⚠ NEW REQUESTS: " \+ newCount\)',
     'System.out.println(LanguageManager.get("techsupport.newRequests", newCount))'),
    (r'System\.out\.println\("  " \+ newStatus \+ " REQUEST"\)',
     'System.out.println(LanguageManager.get("techsupport.requestStatus", newStatus))'),
]

def ensure_import(text):
    if "LanguageManager" in text:
        return text
    return re.sub(
        r"(package [\w.]+;)\n",
        r"\1\n\nimport kz.synapse.utils.LanguageManager;\n",
        text,
        count=1,
    )

for path in glob.glob(ROOT + "/**/*.java", recursive=True):
    text = open(path, encoding="utf-8").read()
    orig = text
    for pat, repl in REPLACEMENTS:
        text = re.sub(pat, repl, text)
    if text != orig:
        text = ensure_import(text)
        open(path, "w", encoding="utf-8", newline="\n").write(text)
        print("Fixed", path)
