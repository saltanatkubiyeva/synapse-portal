package kz.synapse;

import kz.synapse.database.Database;
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
        if (!Database.getInstance().getUsers().isEmpty()) return;

        System.out.println("  First launch — creating test data...");

        // ── Admin ─────────────────────────────────────────────────────
        UserFactory.createAdmin(
                "admin-001", "Admin", "admin@uni.kz", "admin", Language.EN);

        // ── Students (Bachelor) ───────────────────────────────────────
        UserFactory.createStudent("s-001", "Alua Kudaibergenova",
                "al_kudaibergenova@uni.kz", "alua123", Language.EN,
                kz.synapse.enums.School.SITE, 2);
        UserFactory.createStudent("s-002", "Ingkar Farkhatkyzy",
                "inko@uni.kz", "ingkar123", Language.EN,
                kz.synapse.enums.School.SITE, 1);
        UserFactory.createStudent("s-003", "Zhanel Kitarova",
                "zh_kitarova@uni.kz", "zhanel123", Language.EN,
                kz.synapse.enums.School.BS, 3);

        // ── Graduate Student (MASTER) ─────────────────────────────────
        // автоматически получает Researcher роль
        UserFactory.createGraduateStudent("gs-001", "Elsa Yanke",
                "e_yanke@uni.kz", "elsa123", Language.EN,
                kz.synapse.enums.School.SITE,
                kz.synapse.enums.GraduateDegree.MASTER);

        // ── Teacher (Professor) ───────────────────────────────────────
        // профессор автоматически получает Researcher роль
        UserFactory.createProfessor("t-001", "Pakizar Shamoi",
                "pakita@uni.kz", "pakizar123", Language.EN,
                kz.synapse.enums.School.SITE);

        // ── Research Coordinator ──────────────────────────────────────
        UserFactory.createResearchCoordinator("rc-001", "Malika Imekeshova",
                "r_c@uni.kz", "malika123", Language.EN);

        // ── Tech Support ──────────────────────────────────────────────
        UserFactory.createTechSupport("ts-001", "Damir Ten",
                "t_c@uni.kz", "damir123", Language.EN);

        // ── OR Manager ────────────────────────────────────────────────
        UserFactory.createORManager("or-001", "Daneliya Kadyrbayeva",
                "or@uni.kz", "daneliya123", Language.EN);

        // ── School Manager (SITE) ─────────────────────────────────────
        UserFactory.createSchoolManager("sm-001", "Aya Zhambyl",
                "site@uni.kz", "aya123", Language.EN,
                kz.synapse.enums.School.SITE);

        // ── Dean (SITE) ───────────────────────────────────────────────
        UserFactory.createDean("d-001", "Aidyn Aman",
                "dean_site@uni.kz", "aidyn123", Language.EN,
                kz.synapse.enums.School.SITE);

        Database.getInstance().save();

        System.out.println("  Test data created. Login credentials:");
        System.out.println("  admin@uni.kz           / admin");
        System.out.println("  al_kudaibergenova@uni.kz / alua123   (Student)");
        System.out.println("  pakita@uni.kz          / pakizar123  (Teacher/Professor)");
        System.out.println("  or@uni.kz              / daneliya123 (ORManager)");
        System.out.println("  site@uni.kz            / aya123      (SchoolManager)");
        System.out.println("  dean_site@uni.kz       / aidyn123    (Dean)");
        System.out.println("  r_c@uni.kz             / malika123   (ResearchCoordinator)");
        System.out.println("  t_c@uni.kz             / damir123    (TechSupport)");
        System.out.println("  e_yanke@uni.kz         / elsa123     (GraduateStudent)");
        System.out.println();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }
}