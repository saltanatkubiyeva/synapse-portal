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

        System.out.println(LanguageManager.get("setup.firstLaunch"));
        UserFactory.createAdmin(
                "admin-001", "Admin", "admin@uni.kz", "admin",
                Language.EN
        );
        Database.getInstance().save();
        System.out.println(LanguageManager.get("setup.defaultAdmin"));
        System.out.println(LanguageManager.get("setup.addUsersHint"));

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }
}
