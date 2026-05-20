package kz.synapse;

import kz.synapse.database.Database;
import kz.synapse.enums.SemesterType;
import kz.synapse.enums.TeacherPosition;
import kz.synapse.enums.Language;
import kz.synapse.exceptions.AuthenticationException;
import kz.synapse.exceptions.UnauthorizedAccessException;
import kz.synapse.factory.UserFactory;
import kz.synapse.models.*;
import kz.synapse.services.AuthService;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;
import kz.synapse.ui.menus.*;
import kz.synapse.utils.LanguageManager;

public class Main {

    public static void main(String[] args) {

        // загрузка базы
        Database.load();
        seedIfEmpty();

        // главный цикл
        while (true) {
            showWelcome();
            int choice = ConsoleUtils.readInt(UIStrings.get("prompt.choice"));
            switch (choice) {
                case 1 -> login();
                case 2 -> selectLanguage();
                case 3 -> {
                    ConsoleUtils.clearScreen();
                    System.out.println("\n  " + UIStrings.get("msg.goodbye") + "\n");
                    Database.getInstance().save();
                    return;
                }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private static void showWelcome() {
        ConsoleUtils.clearScreen();
        System.out.println();
        System.out.println(LanguageManager.get("app.banner.top"));
        System.out.println(LanguageManager.get("app.banner.empty"));
        System.out.println(LanguageManager.get("app.banner.title"));
        System.out.println(LanguageManager.get("app.banner.empty"));
        System.out.println(LanguageManager.get("app.banner.subtitle"));
        System.out.println(LanguageManager.get("app.banner.empty"));
        System.out.println(LanguageManager.get("app.banner.bottom"));
        System.out.println();
        ConsoleUtils.printLine();
        System.out.println("  " + UIStrings.get("menu.login"));
        System.out.println("  " + UIStrings.get("menu.language"));
        System.out.println("  " + UIStrings.get("menu.exit"));
        ConsoleUtils.printLine();
    }

    private static void login() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("login.title"));
        ConsoleUtils.printLine();
        String email = ConsoleUtils.readLine(UIStrings.get("prompt.email"));
        String pass  = ConsoleUtils.readLine(UIStrings.get("prompt.password"));

        try {
            User user = AuthService.getInstance().login(email, pass);
            System.out.println("\n  " + UIStrings.get("msg.welcome") + user.getName() + "!\n");
            Database.getInstance().save();
            routeToMenu(user);
        } catch (AuthenticationException e) {
            ConsoleUtils.error("Invalid email or password.");
            ConsoleUtils.pressEnter();
        } catch (UnauthorizedAccessException e) {
            ConsoleUtils.error("Your account is banned.");
            ConsoleUtils.pressEnter();
        }

        AuthService.getInstance().logout();
        Database.getInstance().save();
    }

    private static void routeToMenu(User user) {
        if (user instanceof Admin)
            new AdminMenu((Admin) user).show();
        else if (user instanceof ORManager)
            new ORManagerMenu((ORManager) user).show();
        else if (user instanceof SchoolManager)
            new SchoolManagerMenu((SchoolManager) user).show();
        else if (user instanceof Dean)
            new DeanMenu((Dean) user).show();
        else if (user instanceof ResearchCoordinator)
            new ResearchCoordinatorMenu((ResearchCoordinator) user).show();
        else if (user instanceof TechSupportSpecialist)
            new TechSupportMenu((TechSupportSpecialist) user).show();
        else if (user instanceof GraduateStudent)
            new StudentMenu((GraduateStudent) user).show();
        else if (user instanceof Student)
            new StudentMenu((Student) user).show();
        else if (user instanceof Teacher)
            new TeacherMenu((Teacher) user).show();
        else {
            ConsoleUtils.error("No menu available for this role.");
            ConsoleUtils.pressEnter();
        }
    }

    private static void selectLanguage() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println("  " + UIStrings.get("lang.select"));
        ConsoleUtils.printLine();
        System.out.println("  " + UIStrings.get("lang.en"));
        System.out.println("  " + UIStrings.get("lang.ru"));
        System.out.println("  " + UIStrings.get("lang.kz"));
        System.out.println("  " + UIStrings.get("msg.back"));
        ConsoleUtils.printLine();

        switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
            case 1 -> { LanguageManager.getInstance().switchLanguage(Language.EN);
                System.out.println("  " + UIStrings.get("lang.changed")); }
            case 2 -> { LanguageManager.getInstance().switchLanguage(Language.RU);
                System.out.println("  " + UIStrings.get("lang.changed")); }
            case 3 -> { LanguageManager.getInstance().switchLanguage(Language.KZ);
                System.out.println("  " + UIStrings.get("lang.changed")); }
            case 0 -> { return; }
            default -> System.out.println(UIStrings.get("msg.invalid"));
        }
        ConsoleUtils.pressEnter();
    }

    private static void seedIfEmpty() {
        if (Database.getInstance().findByEmail("admin@uni.kz") != null) return;

        System.out.println("  First launch — creating test data...");

        // ── Admin ─────────────────────────────────────────────────────
        UserFactory.createAdmin("admin-001", "Admin",
                "admin@uni.kz", "admin", Language.EN);

        // ── Students (Bachelor, SITE) ─────────────────────────────────
        UserFactory.createStudent("s-001", "Alua Kudaibergenova",
                "alua@uni.kz", "alua123", Language.EN,
                kz.synapse.enums.School.SITE, 2);
        UserFactory.createStudent("s-002", "Saltanat Kubiyeva",
                "salta@uni.kz", "salta123", Language.EN,
                kz.synapse.enums.School.SITE, 2);
        UserFactory.createStudent("s-003", "Zhanel Kitarova",
                "zhanel@uni.kz", "zhanel123", Language.EN,
                kz.synapse.enums.School.BS, 3);

        // ── Graduate Student (MASTER) ─────────────────────────────────
        UserFactory.createGraduateStudent("gs-001", "Elsa Yanke",
                "e_yanke@uni.kz", "elsa123", Language.EN,
                kz.synapse.enums.School.SITE,
                kz.synapse.enums.GraduateDegree.MASTER);

        // ── Professors (Researcher автоматически) ─────────────────────
        // OOP Head Lecturer
        ResearcherDecorator<Teacher> pakizar = UserFactory.createProfessor(
                "t-001", "Pakizar Shamoi",
                "pakita@uni.kz", "pakizar123", Language.EN,
                kz.synapse.enums.School.SITE);

        // OOP Lecturer + Practice
        Teacher assylzhan = UserFactory.createTeacher(
                "t-002", "Assylzhan Izbassar",
                "izbassar@uni.kz", "izbassar123", Language.EN,
                kz.synapse.enums.TeacherPosition.SENIOR_LECTOR,
                kz.synapse.enums.School.SITE);

        // Web Dev Head Lecturer
        ResearcherDecorator<Teacher> bobur = UserFactory.createProfessor(
                "t-003", "Bobur Mukhsimbayev",
                "bobur@uni.kz", "bobur123", Language.EN,
                kz.synapse.enums.School.SITE);

        // Web Dev Practice
        Teacher lelila = UserFactory.createTeacher(
                "t-004", "Lelila Beken",
                "beken@uni.kz", "beken123", Language.EN,
                kz.synapse.enums.TeacherPosition.LECTOR,
                kz.synapse.enums.School.SITE);

        // ── Research Coordinator ──────────────────────────────────────
        UserFactory.createResearchCoordinator("rc-001", "Ingkar Farkhatkyzy",
                "r_c@uni.kz", "rc123", Language.EN);

        // ── Tech Support ──────────────────────────────────────────────
        UserFactory.createTechSupport("ts-001", "Damir Ten",
                "t_s@uni.kz", "damir123", Language.EN);

        // ── OR Manager ────────────────────────────────────────────────
        UserFactory.createORManager("or-001", "Daneliya Kadyrbayeva",
                "or@uni.kz", "daneliya123", Language.EN);

        // ── School Manager (SITE) ─────────────────────────────────────
        SchoolManager siteManager = UserFactory.createSchoolManager(
                "sm-001", "Aya Zhambyl",
                "site@uni.kz", "aya123", Language.EN,
                kz.synapse.enums.School.SITE);

        // ── Deans ─────────────────────────────────────────────────────
        UserFactory.createDean("d-001", "Aibek Dzhaksybekov",
                "site_dean@uni.kz", "dean123", Language.EN,
                kz.synapse.enums.School.SITE);
        UserFactory.createDean("d-002", "Gulnara Seitkali",
                "bs_dean@uni.kz", "dean123", Language.EN,
                kz.synapse.enums.School.BS);

        // ── Courses ───────────────────────────────────────────────────
        Course philosophy = new Course("HUM1102", "Philosophy",
                1, 1, kz.synapse.enums.CourseType.MINOR);
        Course webDev = new Course("INFT2205", "Web Development",
                2, 2, kz.synapse.enums.CourseType.MAJOR);
        // OOP: 2 лекции (2ч каждая) + 1 практика (1ч) = 3 кредита
        Course oop = new Course("CSCI2106",
                "Object-Oriented Programming and Design",
                2, 1, kz.synapse.enums.CourseType.MAJOR);

        Database.getInstance().addCourse(philosophy);
        Database.getInstance().addCourse(webDev);
        Database.getInstance().addCourse(oop);

        // ── Open Semester ─────────────────────────────────────────────
        ORManager orMgr = (ORManager) Database.getInstance()
                .findByEmail("or@uni.kz");
        orMgr.openSemester(kz.synapse.enums.SemesterType.FALL);

        CourseOffering oopA = siteManager.createCourseOffering(
                oop, kz.synapse.enums.SemesterType.FALL, 90);
        siteManager.assignHeadLecturer(oopA, pakizar.getInnerUser());

        // Поток A: лекции
        siteManager.addSlot(oopA, new LessonSlot(
                java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                "101", kz.synapse.enums.LessonType.LECTURE,
                pakizar.getInnerUser(), 45));
        siteManager.addSlot(oopA, new LessonSlot(
                java.time.DayOfWeek.WEDNESDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                "102", kz.synapse.enums.LessonType.LECTURE,
                pakizar.getInnerUser(), 45));

        // Поток A: практики
        java.time.DayOfWeek[] pracDays = {
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.THURSDAY,
                java.time.DayOfWeek.THURSDAY,
                java.time.DayOfWeek.FRIDAY,
                java.time.DayOfWeek.FRIDAY
        };
        java.time.LocalTime[] pracTimes = {
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0)
        };
        String[] pracRooms = {"201","202","203","204","205","206"};

        for (int i = 0; i < 6; i++) {
            siteManager.addSlot(oopA, new LessonSlot(
                    pracDays[i],
                    pracTimes[i],
                    pracTimes[i].plusHours(1),
                    pracRooms[i],
                    kz.synapse.enums.LessonType.PRACTICE,
                    assylzhan, 15));
        }

        CourseOffering oopB = siteManager.createCourseOffering(
                oop, kz.synapse.enums.SemesterType.FALL, 90);
        siteManager.assignHeadLecturer(oopB, pakizar.getInnerUser());

        siteManager.addSlot(oopB, new LessonSlot(
                java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(15, 0),
                "103", kz.synapse.enums.LessonType.LECTURE,
                pakizar.getInnerUser(), 45));
        siteManager.addSlot(oopB, new LessonSlot(
                java.time.DayOfWeek.WEDNESDAY,
                java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(15, 0),
                "104", kz.synapse.enums.LessonType.LECTURE,
                pakizar.getInnerUser(), 45));

        java.time.LocalTime[] pracTimesB = {
                java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(15, 0),
                java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(15, 0),
                java.time.LocalTime.of(13, 0),
                java.time.LocalTime.of(15, 0)
        };
        String[] pracRoomsB = {"207","208","209","210","211","212"};

        for (int i = 0; i < 6; i++) {
            siteManager.addSlot(oopB, new LessonSlot(
                    pracDays[i],
                    pracTimesB[i],
                    pracTimesB[i].plusHours(1),
                    pracRoomsB[i],
                    kz.synapse.enums.LessonType.PRACTICE,
                    assylzhan, 15));
        }

        siteManager.publishOffering(oopA);
        siteManager.publishOffering(oopB);

        // ── Web Dev Offering
        CourseOffering webOff = siteManager.createCourseOffering(
                webDev, kz.synapse.enums.SemesterType.FALL, 90);
        siteManager.assignHeadLecturer(webOff, bobur.getInnerUser());
        // Лекции
        siteManager.addSlot(webOff, new LessonSlot(
                java.time.DayOfWeek.TUESDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                "201", kz.synapse.enums.LessonType.LECTURE,
                bobur.getInnerUser(), 45));
        siteManager.addSlot(webOff, new LessonSlot(
                java.time.DayOfWeek.THURSDAY,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                "202", kz.synapse.enums.LessonType.LECTURE,
                bobur.getInnerUser(), 45));

        // Практики —
        java.time.DayOfWeek[] webPracDays = {
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.WEDNESDAY,
                java.time.DayOfWeek.WEDNESDAY,
                java.time.DayOfWeek.FRIDAY,
                java.time.DayOfWeek.FRIDAY
        };
        java.time.LocalTime[] webPracTimes = {
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0),
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(11, 0)
        };
        String[] webPracRooms = {
                "301","302","303","304","305","306"
        };

        for (int i = 0; i < 6; i++) {
            siteManager.addSlot(webOff, new LessonSlot(
                    webPracDays[i],
                    webPracTimes[i],
                    webPracTimes[i].plusHours(2),
                    webPracRooms[i],
                    kz.synapse.enums.LessonType.PRACTICE,
                    lelila, 15));
        }

        siteManager.publishOffering(webOff);

        Database.getInstance().save();

        System.out.println("  ✓ Test data ready. Login credentials:");
        System.out.println("  admin@uni.kz       / admin");
        System.out.println("  alua@uni.kz        / alua123      (Student SITE yr2)");
        System.out.println("  salta@uni.kz       / salta123     (Student SITE yr2)");
        System.out.println("  pakita@uni.kz      / pakizar123   (Professor/Researcher)");
        System.out.println("  izbassar@uni.kz    / izbassar123  (Teacher OOP practice)");
        System.out.println("  bobur@uni.kz       / bobur123     (Professor Web Dev)");
        System.out.println("  beken@uni.kz      / beken123    (Teacher Web Dev practice)");
        System.out.println("  or@uni.kz          / daneliya123  (ORManager)");
        System.out.println("  site@uni.kz        / aya123       (SchoolManager SITE)");
        System.out.println("  site_dean@uni.kz   / dean123      (Dean SITE)");
        System.out.println("  bs_dean@uni.kz     / dean123      (Dean BS)");
        System.out.println("  r_c@uni.kz         / rc123        (ResearchCoordinator)");
        System.out.println("  t_s@uni.kz         / damir123     (TechSupport)");
        System.out.println("  e_yanke@uni.kz     / elsa123      (GraduateStudent MASTER)");
        System.out.println();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }
}