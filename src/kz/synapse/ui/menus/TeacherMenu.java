package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.AttestationPeriod;
import kz.synapse.enums.LessonType;
import kz.synapse.enums.UrgencyLevel;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TeacherMenu {

    private final Teacher teacher;
    private final ResearcherDecorator<?> researcher; // null если не исследователь

    public TeacherMenu(Teacher teacher) {
        this.teacher    = teacher;
        this.researcher = Database.getInstance().getResearcherFor(teacher);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.teacher");
            System.out.println("  " + UIStrings.get("msg.welcome") + teacher.getName()
                    + "  |  " + teacher.getPosition() + "  |  " + teacher.getSchool()
                    + "  |  Rating: " + String.format("%.1f", teacher.getRating()));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.1.my.courses.students"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.add.lesson.score.for.student"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.3.set.final.exam.score.head.lecturer.only"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.4.view.course.report"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.5.mark.attendance"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.6.send.complaint"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.7.send.message.to.employee"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.8.view.messages"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.9.view.news"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.10.view.notifications"));
            if (researcher != null)
                System.out.println(LanguageManager.get("ui.menus.TeacherMenu.11.researcher.menu"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.12.send.tech.request"));
            System.out.println(LanguageManager.get("common.schedule.menuItem", "13"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            int choice = ConsoleUtils.readInt(UIStrings.get("prompt.choice"));
            switch (choice) {
                case 1  -> viewCoursesAndStudents();
                case 2  -> addLessonScore();
                case 3  -> setFinalExam();
                case 4  -> viewCourseReport();
                case 5  -> markAttendance();
                case 6  -> sendComplaint();
                case 7  -> sendMessage();
                case 8  -> viewMessages();
                case 9  -> viewNews();
                case 10 -> viewNotifications();
                case 11 -> { if (researcher != null) new ResearcherMenu(researcher).show(); }
                case 12 -> sendTechRequest();
                case 13 -> ConsoleUtils.viewSemesterSchedule();
                case 0  -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    // 1. Courses & Students

    private void viewCoursesAndStudents() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.my.courses"));
        ConsoleUtils.printLine();
        List<CourseOffering> offerings = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.isTeacherOfOffering(teacher) || o.canSetFinalExam(teacher))
                .collect(Collectors.toList());

        if (offerings.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.TeacherMenu.no.courses.assigned")); ConsoleUtils.pressEnter(); return; }

        for (int i = 0; i < offerings.size(); i++) {
            CourseOffering o = offerings.get(i);
            System.out.printf("  %d. %-30s [%s] | %d students | HeadLect: %s%n",
                    i + 1, o.getCourse().getName(), o.getSemester(),
                    o.getEnrolledStudents().size(),
                    o.canSetFinalExam(teacher) ? "YOU" : o.getHeadLecturer() != null ? o.getHeadLecturer().getName() : "—");
        }
        ConsoleUtils.printLine();
        int idx = ConsoleUtils.readInt("Select course to view students (0=back): ");
        if (idx < 1 || idx > offerings.size()) return;

        CourseOffering offering = offerings.get(idx - 1);
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("teacher.students.in", offering.getCourse().getName()));
        ConsoleUtils.printLine();
        List<Student> students = new java.util.ArrayList<>(offering.getEnrolledStudents());
        if (students.isEmpty()) System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.students.enrolled"));
        else {
            System.out.printf("  %-4s %-25s %-10s %-6s %-6s %-6s%n",
                    "#", "Name", "School", "ATT1", "ATT2", "FINAL");
            ConsoleUtils.printLine();
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                Mark m = offering.getMark(s);
                System.out.printf("  %-4d %-25s %-10s %-6.1f %-6.1f %-6.1f%n",
                        i + 1, s.getName(), s.getSchool(),
                        m != null ? m.getAtt1() : 0.0,
                        m != null ? m.getAtt2() : 0.0,
                        m != null ? m.getFinalExam() : 0.0);
            }
        }
        ConsoleUtils.pressEnter();
    }

    // 2. Add Lesson Score

    private void addLessonScore() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.add.lesson.score")); ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;
        Student student = selectStudent(offering); if (student == null) return;

        System.out.println(LanguageManager.get("common.periodPrompt"));
        AttestationPeriod period = ConsoleUtils.readInt("Choice: ") == 1
                ? AttestationPeriod.ATT1 : AttestationPeriod.ATT2;

        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.lesson.type.1.lecture.2.practice"));
        LessonType type = ConsoleUtils.readInt("Choice: ") == 1
                ? LessonType.LECTURE : LessonType.PRACTICE;

        double score   = ConsoleUtils.readDouble("Score: ");
        String comment = ConsoleUtils.readLine("Comment (optional): ");

        LessonScore ls = new LessonScore(LocalDate.now(), type, period, score,
                comment.isEmpty() ? "" : comment);
        try {
            teacher.addLessonScore(student, offering, ls);
            Database.getInstance().save();
            ConsoleUtils.success("Score added. ATT1: " + offering.getOrCreateMark(student).getAtt1()
                    + "  ATT2: " + offering.getOrCreateMark(student).getAtt2());
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    // 3. Set Final Exam

    private void setFinalExam() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.set.final.exam.score.head.lecturer.only"));
        ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;
        if (!offering.canSetFinalExam(teacher)) {
            ConsoleUtils.error("You are not the Head Lecturer of this course.");
            ConsoleUtils.pressEnter(); return;
        }
        Student student = selectStudent(offering); if (student == null) return;
        double score = ConsoleUtils.readDouble("Final exam score (0-40): ");
        try {
            teacher.setFinalExam(student, offering, score);
            Database.getInstance().save();
            Mark m = offering.getMark(student);
            ConsoleUtils.success("Final set. Total: " + m.getTotal()
                    + " (" + m.getLetterGrade() + ")");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    // 4. Course Report

    private void viewCourseReport() {
        ConsoleUtils.clearScreen();
        CourseOffering offering = selectOffering(); if (offering == null) return;

        // Оценки студентов
        System.out.println(teacher.generateCourseReport(offering));

        // Статистика через CourseStatistics
        kz.synapse.services.CourseStatistics stats =
                new kz.synapse.services.CourseStatistics(offering);
        ConsoleUtils.printLine();
        System.out.printf("  Pass Rate:    %.1f%%%n", stats.getPassRate());
        System.out.printf("  Fail Rate:    %.1f%%%n", stats.getFailRate());
        System.out.printf("  Average GPA:  %.2f%n",  stats.getAvgGpa());
        System.out.println("  Grade Distribution: " + stats.getGradeDistribution());

        // Топ-3 студента
        java.util.List<Student> top = stats.getTopStudents(3);
        if (!top.isEmpty()) {
            ConsoleUtils.printLine();
            System.out.println("  Top Students:");
            for (int i = 0; i < top.size(); i++) {
                Mark m = offering.getMark(top.get(i));
                System.out.printf("  %d. %-25s %.1f (%s)%n",
                        i + 1, top.get(i).getName(),
                        m.getTotal(), m.getLetterGrade());
            }
        }
        ConsoleUtils.pressEnter();
    }

    // 5. Attendance

    private void markAttendance() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.mark.attendance")); ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;
        List<Student> students = new java.util.ArrayList<>(offering.getEnrolledStudents());
        if (students.isEmpty()) { System.out.println(LanguageManager.get("common.noStudents")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < students.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, students.get(i).getName());
        int idx = ConsoleUtils.readInt("Select student (0=cancel): ");
        if (idx < 1 || idx > students.size()) return;
        String present = ConsoleUtils.readLine("Present? (yes/no): ");
        try {
            teacher.markAttendance(offering, LocalDate.now(),
                    students.get(idx - 1), present.equalsIgnoreCase("yes"));
            Database.getInstance().save();
            ConsoleUtils.success("Attendance marked.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    // 6. Complaint

    private void sendComplaint() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.send.complaint")); ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;
        List<Student> students = new java.util.ArrayList<>(offering.getEnrolledStudents());
        if (students.isEmpty()) { System.out.println(LanguageManager.get("common.noStudents")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < students.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, students.get(i).getName());
        int idx = ConsoleUtils.readInt("Select student (0=cancel): ");
        if (idx < 1 || idx > students.size()) return;
        System.out.println(LanguageManager.get("ui.menus.TeacherMenu.urgency.1.low.2.medium.3.high"));
        UrgencyLevel urgency = switch (ConsoleUtils.readInt("Choice: ")) {
            case 2 -> UrgencyLevel.MEDIUM;
            case 3 -> UrgencyLevel.HIGH;
            default -> UrgencyLevel.LOW;
        };
        String reason = ConsoleUtils.readLine("Reason: ");
        teacher.sendComplaint(List.of(students.get(idx - 1)), urgency, reason);
        Database.getInstance().save();
        ConsoleUtils.success("Complaint sent to Dean.");
        ConsoleUtils.pressEnter();
    }

    // 7-8. Messages

    private void sendMessage() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendMessage.title")); ConsoleUtils.printLine();
        List<Employee> employees = Database.getInstance().getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(teacher))
                .map(u -> (Employee) u)
                .collect(Collectors.toList());
        for (int i = 0; i < employees.size(); i++)
            System.out.printf("  %d. %-25s (%s)%n", i + 1,
                    employees.get(i).getName(), employees.get(i).getClass().getSimpleName());
        int idx = ConsoleUtils.readInt("Select recipient (0=cancel): ");
        if (idx < 1 || idx > employees.size()) return;
        String text = ConsoleUtils.readLine("Message: ");
        teacher.sendMessage(employees.get(idx - 1), text);
        Database.getInstance().save();
        ConsoleUtils.success("Message sent.");
        ConsoleUtils.pressEnter();
    }

    private void viewMessages() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.inbox.title"));
        ConsoleUtils.printLine();
        List<Message> inbox = teacher.getUnreadMessages();
        if (inbox.isEmpty()) System.out.println(LanguageManager.get("common.inbox.empty"));
        else inbox.forEach(m -> System.out.println("  " + m));
        ConsoleUtils.pressEnter();
    }

    // 9-10. News & Notifications

    private void sendTechRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendTechRequest.title")); ConsoleUtils.printLine();
        String desc = ConsoleUtils.readLine("Description (0=cancel): ");
        if (desc.equals("0")) return;
        teacher.sendTechRequest(desc);
        Database.getInstance().save();
        ConsoleUtils.success("Tech request submitted.");
        ConsoleUtils.pressEnter();
    }

    private void viewNews() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.newsFeed.title")); ConsoleUtils.printLine();
        Database.getInstance().getNewsList().stream()
                .sorted((a, b) -> Boolean.compare(b.isPinned(), a.isPinned()))
                .forEach(n -> System.out.println((n.isPinned() ? "  📌 " : "     ") + n));
        ConsoleUtils.pressEnter();
    }

    private void viewNotifications() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.notifications.title")); ConsoleUtils.printLine();
        List<Notification> notifs = teacher.getUnreadNotifications();
        if (notifs.isEmpty()) System.out.println(LanguageManager.get("common.notifications.empty"));
        else { notifs.forEach(n -> System.out.println("  " + n)); teacher.markAllNotificationsRead(); }
        ConsoleUtils.pressEnter();
    }

    // helpers

    private CourseOffering selectOffering() {
        List<CourseOffering> offerings = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.isTeacherOfOffering(teacher) || o.canSetFinalExam(teacher))
                .collect(Collectors.toList());
        if (offerings.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.TeacherMenu.no.courses")); ConsoleUtils.pressEnter(); return null; }
        for (int i = 0; i < offerings.size(); i++)
            System.out.printf("  %d. %s [%s]%n", i + 1,
                    offerings.get(i).getCourse().getName(), offerings.get(i).getSemester());
        int idx = ConsoleUtils.readInt("Select course (0=cancel): ");
        if (idx < 1 || idx > offerings.size()) return null;
        return offerings.get(idx - 1);
    }

    private Student selectStudent(CourseOffering offering) {
        List<Student> students = new java.util.ArrayList<>(offering.getEnrolledStudents());
        if (students.isEmpty()) { System.out.println(LanguageManager.get("common.noStudents")); return null; }
        for (int i = 0; i < students.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, students.get(i).getName());
        int idx = ConsoleUtils.readInt("Select student (0=cancel): ");
        if (idx < 1 || idx > students.size()) return null;
        return students.get(idx - 1);
    }
}