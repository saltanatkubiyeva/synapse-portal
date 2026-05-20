package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.factory.UserFactory;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.List;
import java.util.UUID;

public class AdminMenu {

    private final Admin admin;

    public AdminMenu(Admin admin) { this.admin = admin; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.admin");
            System.out.println("  " + UIStrings.get("msg.welcome") + admin.getName());
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.1.manage.users"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.2.view.all.logs"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.3.view.system.logs"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.4.view.user.activity.logs"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.5.reset.user.password"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.6.ban.unban.user"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> manageUsers();
                case 2 -> viewLogs(admin.viewLogs(), "ALL LOGS");
                case 3 -> viewLogs(admin.viewSystemLogs(), "SYSTEM LOGS");
                case 4 -> viewLogs(admin.viewUserLogs(), "USER ACTIVITY LOGS");
                case 5 -> resetPassword();
                case 6 -> banUnban();
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void manageUsers() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.manage.users"));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.1.view.all.users"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.2.add.student.bachelor"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.3.add.graduate.student.master.phd"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.4.add.teacher.professor"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.5.add.or.manager"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.6.add.school.manager"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.7.add.tech.support"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.8.add.research.coordinator"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.9.add.dean"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.10.remove.user"));
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.11.update.user.info"));
            System.out.println("  " + UIStrings.get("msg.back"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1  -> viewAllUsers();
                case 2  -> addStudent();
                case 3  -> addGraduateStudent();
                case 4  -> addTeacher();
                case 5  -> addORManager();
                case 6  -> addSchoolManager();
                case 7  -> addTechSupport();
                case 8  -> addResearchCoordinator();
                case 9  -> addDean();
                case 10 -> removeUser();
                case 11 -> updateUser();
                case 0  -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void viewAllUsers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.all.users"));
        ConsoleUtils.printLine();
        List<User> users = admin.viewAllUsers();
        if (users.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); }
        else {
            System.out.printf("  %-4s %-25s %-30s %-22s %-6s%n",
                    "#", "Name", "Email", "Role", "Banned");
            ConsoleUtils.printLine();
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                System.out.printf("  %-4d %-25s %-30s %-22s %-6s%n",
                        i + 1, u.getName(), u.getEmail(),
                        u.getClass().getSimpleName(),
                        u.isBanned() ? "YES" : "-");
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void addStudent() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.student")); ConsoleUtils.printLine();
        String id    = UUID.randomUUID().toString().substring(0, 8);
        String name  = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email (@uni.kz): ");
        String pass  = ConsoleUtils.readLine("Password: ");
        kz.synapse.enums.School school = selectSchool();
        try {
            UserFactory.createStudent(id, name, email, pass, kz.synapse.enums.Language.EN, school);
            Database.getInstance().save();
            ConsoleUtils.success("Student " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addGraduateStudent() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.graduate.student.master.phd")); ConsoleUtils.printLine();
        String id    = UUID.randomUUID().toString().substring(0, 8);
        String name  = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email (@uni.kz): ");
        String pass  = ConsoleUtils.readLine("Password: ");
        kz.synapse.enums.School school = selectSchool();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.degree.1.master.2.phd"));
        kz.synapse.enums.GraduateDegree degree = ConsoleUtils.readInt("Choice: ") == 1
                ? kz.synapse.enums.GraduateDegree.MASTER
                : kz.synapse.enums.GraduateDegree.PHD;
        try {
            kz.synapse.models.ResearcherDecorator<kz.synapse.models.GraduateStudent> decorator =
                    UserFactory.createGraduateStudent(id, name, email, pass,
                            kz.synapse.enums.Language.EN, school, degree);
            Database.getInstance().save();
            ConsoleUtils.success("Graduate Student " + name + " (" + degree + ") created.");
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.researcher.role.assigned.automatically"));
            ConsoleUtils.printLine();

            // назначение супервайзора
            assignSupervisor(decorator.getInnerUser());

        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    /** назначает супервайзора — делегирует логику в researchcoordinatormenu. */
    private void assignSupervisor(kz.synapse.models.GraduateStudent gs) {
        // ищем Research Coordinator в системе
        kz.synapse.models.ResearchCoordinator rc =
                Database.getInstance().getUsers().stream()
                        .filter(u -> u instanceof kz.synapse.models.ResearchCoordinator)
                        .map(u -> (kz.synapse.models.ResearchCoordinator) u)
                        .findFirst().orElse(null);

        if (rc != null) {
            new kz.synapse.ui.menus.ResearchCoordinatorMenu(rc)
                    .assignOrChangeSupervisor(gs);
        } else {
            // если координатора ещё нет — показываем простой список
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.AdminMenu.assign.supervisor.no.research.coordinator.in.syste"));
            List<kz.synapse.models.ResearcherDecorator<?>> eligible =
                    Database.getInstance().getResearchers().stream()
                            .filter(r -> r.calculateHIndex() >= 3
                                    && !r.getInnerUser().equals(gs))
                            .collect(java.util.stream.Collectors.toList());
            if (eligible.isEmpty()) {
                System.out.println(LanguageManager.get("ui.menus.AdminMenu.no.eligible.supervisors.h.index.3.assign.later"));
                ConsoleUtils.pressEnter(); return;
            }
            for (int i = 0; i < eligible.size(); i++)
                System.out.printf("  %d. %-25s h-index:%d%n",
                        i + 1, eligible.get(i).getName(),
                        eligible.get(i).calculateHIndex());
            System.out.println(LanguageManager.get("common.skip"));
            int idx = ConsoleUtils.readInt("Select: ");
            if (idx == 0 || idx > eligible.size()) return;
            try {
                gs.setSupervisor(eligible.get(idx - 1));
                Database.getInstance().save();
                ConsoleUtils.success("Supervisor: " + eligible.get(idx - 1).getName());
            } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        }
    }

    private void addTeacher() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.teacher")); ConsoleUtils.printLine();
        String id    = UUID.randomUUID().toString().substring(0, 8);
        String name  = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email: ");
        if (email.equals("0")) return;
        String pass  = ConsoleUtils.readLine("Password: ");
        if (pass.equals("0")) return;
        kz.synapse.enums.School school = selectSchool();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.position.1.tutor.2.lector.3.senior.lector.4.profes"));
        kz.synapse.enums.TeacherPosition pos = switch (ConsoleUtils.readInt("Choice: ")) {
            case 1 -> kz.synapse.enums.TeacherPosition.TUTOR;
            case 2 -> kz.synapse.enums.TeacherPosition.LECTOR;
            case 3 -> kz.synapse.enums.TeacherPosition.SENIOR_LECTOR;
            default -> kz.synapse.enums.TeacherPosition.PROFESSOR;
        };
        try {
            if (pos == kz.synapse.enums.TeacherPosition.PROFESSOR)
                UserFactory.createProfessor(id, name, email, pass, kz.synapse.enums.Language.EN, school);
            else
                UserFactory.createTeacher(id, name, email, pass, kz.synapse.enums.Language.EN, pos, school);
            Database.getInstance().save();
            ConsoleUtils.success("Teacher " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addORManager() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.or.manager")); ConsoleUtils.printLine();
        String id = UUID.randomUUID().toString().substring(0, 8);
        String name = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email: ");
        if (email.equals("0")) return;
        String pass = ConsoleUtils.readLine("Password: ");
        if (pass.equals("0")) return;
        try {
            UserFactory.createORManager(id, name, email, pass, kz.synapse.enums.Language.EN);
            Database.getInstance().save();
            ConsoleUtils.success("OR Manager " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addSchoolManager() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.school.manager")); ConsoleUtils.printLine();
        String id = UUID.randomUUID().toString().substring(0, 8);
        String name = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email: ");
        if (email.equals("0")) return;
        String pass = ConsoleUtils.readLine("Password: ");
        if (pass.equals("0")) return;
        kz.synapse.enums.School school = selectSchool();
        try {
            UserFactory.createSchoolManager(id, name, email, pass, kz.synapse.enums.Language.EN, school);
            Database.getInstance().save();
            ConsoleUtils.success("School Manager " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addTechSupport() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.tech.support")); ConsoleUtils.printLine();
        String id = UUID.randomUUID().toString().substring(0, 8);
        String name = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email: ");
        if (email.equals("0")) return;
        String pass = ConsoleUtils.readLine("Password: ");
        if (pass.equals("0")) return;
        try {
            UserFactory.createTechSupport(id, name, email, pass, kz.synapse.enums.Language.EN);
            Database.getInstance().save();
            ConsoleUtils.success("Tech Support " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addResearchCoordinator() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.research.coordinator")); ConsoleUtils.printLine();
        String id = UUID.randomUUID().toString().substring(0, 8);
        String name = ConsoleUtils.readLine("Full Name (0=cancel): ");
        if (name.equals("0")) return;
        String email = ConsoleUtils.readLine("Email: ");
        if (email.equals("0")) return;
        String pass = ConsoleUtils.readLine("Password: ");
        if (pass.equals("0")) return;
        try {
            UserFactory.createResearchCoordinator(id, name, email, pass, kz.synapse.enums.Language.EN);
            Database.getInstance().save();
            ConsoleUtils.success("Research Coordinator " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addDean() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.AdminMenu.add.dean")); ConsoleUtils.printLine();
        String id = UUID.randomUUID().toString().substring(0, 8);
        String name = ConsoleUtils.readLine("Full Name: ");
        String email = ConsoleUtils.readLine("Email: ");
        String pass = ConsoleUtils.readLine("Password: ");
        kz.synapse.enums.School school = selectSchool();
        try {
            UserFactory.createDean(id, name, email, pass, kz.synapse.enums.Language.EN, school);
            Database.getInstance().save();
            ConsoleUtils.success("Dean " + name + " created.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void removeUser() {
        viewAllUsers();
        List<User> users = admin.viewAllUsers();
        if (users.isEmpty()) return;
        int idx = ConsoleUtils.readInt("User # to remove (0=cancel): ");
        if (idx < 1 || idx > users.size()) return;
        User target = users.get(idx - 1);
        // защита: нельзя удалить Admin
        if (target instanceof Admin) {
            ConsoleUtils.error("Cannot remove an Admin account.");
            ConsoleUtils.pressEnter();
            return;
        }
        admin.removeUser(target);
        Database.getInstance().save();
        ConsoleUtils.success(target.getName() + " removed.");
        ConsoleUtils.pressEnter();
    }

    private void updateUser() {
        viewAllUsers();
        List<User> users = admin.viewAllUsers();
        if (users.isEmpty()) return;
        int idx = ConsoleUtils.readInt("User # to update (0=cancel): ");
        if (idx < 1 || idx > users.size()) return;
        User target = users.get(idx - 1);
        String newName  = ConsoleUtils.readLine("New name (Enter=keep '" + target.getName() + "'): ");
        String newEmail = ConsoleUtils.readLine("New email (Enter=keep '" + target.getEmail() + "'): ");
        admin.updateUser(target,
                newName.isEmpty() ? null : newName,
                newEmail.isEmpty() ? null : newEmail);
        Database.getInstance().save();
        ConsoleUtils.success("User updated.");
        ConsoleUtils.pressEnter();
    }

    private void viewLogs(List<String> logs, String title) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println("  " + title);
        ConsoleUtils.printLine();
        if (logs.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else logs.forEach(l -> System.out.println("  " + l));
        ConsoleUtils.pressEnter();
    }

    private void resetPassword() {
        viewAllUsers();
        List<User> users = admin.viewAllUsers();
        if (users.isEmpty()) return;
        int idx = ConsoleUtils.readInt("User # (0=cancel): ");
        if (idx < 1 || idx > users.size()) return;
        admin.resetPassword(users.get(idx - 1));
        Database.getInstance().save();
        ConsoleUtils.pressEnter();
    }

    private void banUnban() {
        viewAllUsers();
        List<User> users = admin.viewAllUsers();
        if (users.isEmpty()) return;
        int idx = ConsoleUtils.readInt("User # (0=cancel): ");
        if (idx < 1 || idx > users.size()) return;
        User target = users.get(idx - 1);
        if (target.isBanned()) {
            admin.unbanUser(target);
            ConsoleUtils.success(target.getName() + " unbanned.");
        } else {
            admin.banUser(target);
            ConsoleUtils.success(target.getName() + " banned.");
        }
        Database.getInstance().save();
        ConsoleUtils.pressEnter();
    }

    private kz.synapse.enums.School selectSchool() {
        System.out.println(LanguageManager.get("common.schoolPrompt"));
        return switch (ConsoleUtils.readInt("Choice: ")) {
            case 1 -> kz.synapse.enums.School.SITE;
            case 2 -> kz.synapse.enums.School.BS;
            case 3 -> kz.synapse.enums.School.ISE;
            case 4 -> kz.synapse.enums.School.KMA;
            case 5 -> kz.synapse.enums.School.OAG;
            case 6 -> kz.synapse.enums.School.SGE;
            case 7 -> kz.synapse.enums.School.SAM;
            default -> kz.synapse.enums.School.SCHE;
        };
    }
}