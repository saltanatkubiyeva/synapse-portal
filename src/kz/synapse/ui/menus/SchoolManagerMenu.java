package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.*;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SchoolManagerMenu {

    private final SchoolManager manager;

    public SchoolManagerMenu(SchoolManager manager) { this.manager = manager; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.schoolmanager");
            System.out.println("  " + UIStrings.get("msg.welcome") + manager.getName()
                    + "  |  School: " + manager.getSchool());
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.1.create.course.offering"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.2.assign.head.lecturer"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.3.add.lesson.slot"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.4.publish.course.offering"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.5.view.offerings"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.6.view.students"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.7.view.teachers"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.8.performance.report"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.9.publish.school.news"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.10.add.course.to.catalog"));
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.11.assign.teaching.assistant"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.12.send.tech.request"));
            System.out.println(LanguageManager.get("common.schedule.menuItem", "13"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> createOffering();
                case 2 -> assignHeadLecturer();
                case 3 -> addSlot();
                case 4 -> publishOffering();
                case 5 -> viewOfferings();
                case 6 -> viewStudents();
                case 7 -> viewTeachers();
                case 8 -> { System.out.println(manager.createPerformanceReport()); ConsoleUtils.pressEnter(); }
                case 9 -> publishNews();
                case 10 -> addCourseToCatalog();
                case 11 -> assignTA();
                case 12 -> sendTechRequest();
                case 13 -> ConsoleUtils.viewSemesterSchedule();
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void createOffering() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.create.course.offering")); ConsoleUtils.printLine();

        List<Course> courses = Database.getInstance().getCourses();
        if (courses.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.courses.in.catalog")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < courses.size(); i++)
            System.out.printf("  %d. %s (%d cr: %dL+%dP)%n", i + 1,
                    courses.get(i).getName(), courses.get(i).getCredits(),
                    courses.get(i).getLecturesPerWeek(), courses.get(i).getPracticesPerWeek());

        int idx = ConsoleUtils.readInt("Select course (0=cancel): ");
        if (idx == 0 || idx > courses.size()) return;
        Course course = courses.get(idx - 1);

        System.out.println(LanguageManager.get("common.semesterPrompt"));
        SemesterType sem = switch (ConsoleUtils.readInt("Choice: ")) {
            case 2 -> SemesterType.SPRING;
            case 3 -> SemesterType.SUMMER;
            default -> SemesterType.FALL;
        };
        int max  = ConsoleUtils.readInt("Max students: ");

        try {
            CourseOffering offering = manager.createCourseOffering(course, sem, max);
            Database.getInstance().save();
            ConsoleUtils.success("Offering created: " + offering);
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.next.assign.head.lecturer.and.add.lesson.slots"));
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void assignHeadLecturer() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.assign.head.lecturer")); ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;
        List<Teacher> teachers = manager.viewTeachers(Comparator.naturalOrder());
        if (teachers.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.teachers")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < teachers.size(); i++)
            System.out.printf("  %d. %s (%s)%n", i + 1,
                    teachers.get(i).getName(), teachers.get(i).getPosition());
        int idx = ConsoleUtils.readInt("Select head lecturer (0=cancel): ");
        if (idx < 1 || idx > teachers.size()) return;
        manager.assignHeadLecturer(offering, teachers.get(idx - 1));
        Database.getInstance().save();
        ConsoleUtils.success("Head Lecturer assigned: " + teachers.get(idx - 1).getName());
        ConsoleUtils.pressEnter();
    }

    private void addSlot() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.add.lesson.slot")); ConsoleUtils.printLine();
        CourseOffering offering = selectOffering(); if (offering == null) return;

        System.out.println(LanguageManager.get("schoolmanager.requirements",
                offering.getCourse().getLecturesPerWeek(),
                offering.getCourse().getPracticesPerWeek()));
        System.out.println(LanguageManager.get("schoolmanager.currentSlots", offering.getSlots().size()));
        ConsoleUtils.printLine();

        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.type.1.lecture.2.practice"));
        LessonType type = ConsoleUtils.readInt("Choice: ") == 1
                ? LessonType.LECTURE : LessonType.PRACTICE;

        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.day.1.mon.2.tue.3.wed.4.thu.5.fri.6.sat"));
        DayOfWeek day = switch (ConsoleUtils.readInt("Choice: ")) {
            case 1 -> DayOfWeek.MONDAY; case 2 -> DayOfWeek.TUESDAY;
            case 3 -> DayOfWeek.WEDNESDAY; case 4 -> DayOfWeek.THURSDAY;
            case 5 -> DayOfWeek.FRIDAY; default -> DayOfWeek.SATURDAY;
        };

        String startStr = ConsoleUtils.readLine("Start time (HH:MM): ");
        String endStr   = ConsoleUtils.readLine("End time (HH:MM): ");
        String room     = ConsoleUtils.readLine("Room: ");
        int maxSeats    = ConsoleUtils.readInt("Max seats in this slot: ");

        List<Teacher> teachers = manager.viewTeachers(Comparator.naturalOrder());
        for (int i = 0; i < teachers.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, teachers.get(i).getName());
        int idx = ConsoleUtils.readInt("Select teacher for this slot (0=cancel): ");
        if (idx < 1 || idx > teachers.size()) return;

        try {
            LessonSlot slot = new LessonSlot(day,
                    LocalTime.parse(startStr), LocalTime.parse(endStr),
                    room, type, teachers.get(idx - 1), maxSeats);
            manager.addSlot(offering, slot);
            Database.getInstance().save();
            ConsoleUtils.success("Slot added: " + slot);
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void publishOffering() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.publish.course.offering")); ConsoleUtils.printLine();
        List<CourseOffering> unpublished = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.getTargetSchool() == manager.getSchool()
                        && !Database.getInstance().getPublishedOfferings().contains(o))
                .collect(Collectors.toList());
        if (unpublished.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.offerings.to.publish")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < unpublished.size(); i++)
            System.out.printf("  %d. %s | slots: %d | headLect: %s%n",
                    i + 1, unpublished.get(i),
                    unpublished.get(i).getSlots().size(),
                    unpublished.get(i).getHeadLecturer() != null
                            ? unpublished.get(i).getHeadLecturer().getName() : "NOT SET");
        int idx = ConsoleUtils.readInt("Select to publish (0=cancel): ");
        if (idx < 1 || idx > unpublished.size()) return;
        try {
            manager.publishOffering(unpublished.get(idx - 1));
            Database.getInstance().save();
            ConsoleUtils.success("Offering published. Students can now register.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void viewOfferings() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("schoolmanager.offerings.title", manager.getSchool())); ConsoleUtils.printLine();
        List<CourseOffering> offerings = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.getTargetSchool() == manager.getSchool())
                .collect(Collectors.toList());
        if (offerings.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else offerings.forEach(o -> System.out.println("  " + o
                + (Database.getInstance().getPublishedOfferings().contains(o) ? " [PUBLISHED]" : " [draft]")));
        ConsoleUtils.pressEnter();
    }

    private void viewStudents() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("schoolmanager.students.title", manager.getSchool())); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.sort.1.by.gpa.2.alphabetically"));
        Comparator<Student> comp = ConsoleUtils.readInt("Choice: ") == 1
                ? Comparator.reverseOrder() : Comparator.comparing(Student::getName);
        List<Student> students = manager.viewStudents(comp);
        if (students.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else students.forEach(s -> System.out.printf("  %-25s GPA:%.2f  Credits:%d%n",
                s.getName(), s.getGpa(), s.getSemesterCredits()));
        ConsoleUtils.pressEnter();
    }

    private void viewTeachers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("schoolmanager.teachers.title", manager.getSchool())); ConsoleUtils.printLine();
        manager.viewTeachers(Comparator.naturalOrder())
                .forEach(t -> System.out.printf("  %-25s %-15s Rating:%.1f%n",
                        t.getName(), t.getPosition(), t.getRating()));
        ConsoleUtils.pressEnter();
    }

    private void publishNews() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.publish.school.news")); ConsoleUtils.printLine();
        String title   = ConsoleUtils.readLine("Title: ");
        String content = ConsoleUtils.readLine("Content: ");
        manager.addNews(title, content, NewsType.ANNOUNCEMENT);
        Database.getInstance().save();
        ConsoleUtils.success("News published.");
        ConsoleUtils.pressEnter();
    }

    private void assignTA() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.assign.teaching.assistant")); ConsoleUtils.printLine();

        // 1. Выбираем GraduateStudent из школы
        List<Student> allStudents = manager.viewStudents(Comparator.naturalOrder());
        List<kz.synapse.models.GraduateStudent> gradStudents = allStudents.stream()
                .filter(s -> s instanceof kz.synapse.models.GraduateStudent)
                .map(s -> (kz.synapse.models.GraduateStudent) s)
                .collect(Collectors.toList());

        if (gradStudents.isEmpty()) {
            System.out.println(LanguageManager.get("schoolmanager.noGraduates", manager.getSchool()));
            ConsoleUtils.pressEnter(); return;
        }

        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.graduate.students"));
        for (int i = 0; i < gradStudents.size(); i++)
            System.out.printf("  %d. %-25s [%s] isTA: %s%n",
                    i + 1, gradStudents.get(i).getName(),
                    gradStudents.get(i).getDegree(),
                    gradStudents.get(i).isTA() ? "YES" : "no");
        System.out.println(LanguageManager.get("common.cancel"));
        int gsIdx = ConsoleUtils.readInt("Select graduate student: ");
        if (gsIdx == 0 || gsIdx > gradStudents.size()) return;
        kz.synapse.models.GraduateStudent gs = gradStudents.get(gsIdx - 1);

        // 2. Выбираем CourseOffering
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.course.offerings"));
        List<CourseOffering> offerings = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.getTargetSchool() == manager.getSchool())
                .collect(Collectors.toList());

        if (offerings.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.offerings.available")); ConsoleUtils.pressEnter(); return;
        }
        for (int i = 0; i < offerings.size(); i++)
            System.out.printf("  %d. %-30s [%s]%n",
                    i + 1, offerings.get(i).getCourse().getName(), offerings.get(i).getSemester());
        System.out.println(LanguageManager.get("common.cancel"));
        int ofIdx = ConsoleUtils.readInt("Select offering: ");
        if (ofIdx == 0 || ofIdx > offerings.size()) return;
        CourseOffering offering = offerings.get(ofIdx - 1);

        // 3. Выбираем Teacher из слотов офферинга
        ConsoleUtils.printLine();
        List<Teacher> teachersInOffering = offering.getSlots().stream()
                .map(LessonSlot::getTeacher)
                .distinct()
                .collect(Collectors.toList());

        if (teachersInOffering.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.teachers.assigned.to.this.offering.yet"));
            ConsoleUtils.pressEnter(); return;
        }
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.teachers.in.this.offering"));
        for (int i = 0; i < teachersInOffering.size(); i++) {
            Teacher t = teachersInOffering.get(i);
            long slotCount = offering.getSlotsByTeacher(t).size();
            System.out.printf("  %d. %-25s | %d slot(s)%n", i + 1, t.getName(), slotCount);
        }
        System.out.println(LanguageManager.get("common.cancel"));
        int tIdx = ConsoleUtils.readInt("Select teacher to assist: ");
        if (tIdx == 0 || tIdx > teachersInOffering.size()) return;
        Teacher assistedTeacher = teachersInOffering.get(tIdx - 1);

        // 4. Назначаем
        try {
            kz.synapse.models.TeachingAssistant ta =
                    manager.assignTA(gs, offering, assistedTeacher);
            Database.getInstance().save();
            ConsoleUtils.success(gs.getName() + " assigned as TA for "
                    + assistedTeacher.getName() + " in "
                    + offering.getCourse().getName());
            System.out.println(LanguageManager.get("schoolmanager.accessibleSlots", ta.getAccessibleSlots().size()));
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addCourseToCatalog() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.add.course.to.catalog")); ConsoleUtils.printLine();
        String code = ConsoleUtils.readLine("Course code (e.g. CS101, 0=cancel): ");
        if (code.equals("0")) return;
        String name = ConsoleUtils.readLine("Course name: ");
        int lectures  = ConsoleUtils.readInt("Lectures per week: ");
        int practices = ConsoleUtils.readInt("Practices per week: ");
        System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.type.1.major.2.minor.3.free.elective"));
        kz.synapse.enums.CourseType type = switch (ConsoleUtils.readInt("Choice: ")) {
            case 2 -> kz.synapse.enums.CourseType.MINOR;
            case 3 -> kz.synapse.enums.CourseType.FREE_ELECTIVE;
            default -> kz.synapse.enums.CourseType.MAJOR;
        };
        Course course = new Course(code, name, lectures, practices, type);
        Database.getInstance().addCourse(course);
        Database.getInstance().save();
        ConsoleUtils.success("Course added: " + course);
        ConsoleUtils.pressEnter();
    }

    private void sendTechRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendTechRequest.title")); ConsoleUtils.printLine();
        String desc = ConsoleUtils.readLine("Description (0=cancel): ");
        if (desc.equals("0")) return;
        manager.sendTechRequest(desc);
        Database.getInstance().save();
        ConsoleUtils.success("Tech request submitted.");
        ConsoleUtils.pressEnter();
    }

    private CourseOffering selectOffering() {
        List<CourseOffering> offerings = Database.getInstance().getCourseOfferings().stream()
                .filter(o -> o.getTargetSchool() == manager.getSchool())
                .collect(Collectors.toList());
        if (offerings.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.SchoolManagerMenu.no.offerings")); ConsoleUtils.pressEnter(); return null; }
        for (int i = 0; i < offerings.size(); i++)
            System.out.printf("  %d. %s [%s]%n", i + 1,
                    offerings.get(i).getCourse().getName(), offerings.get(i).getSemester());
        int idx = ConsoleUtils.readInt("Select offering (0=cancel): ");
        if (idx < 1 || idx > offerings.size()) return null;
        return offerings.get(idx - 1);
    }
}