package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.List;
import java.util.stream.Collectors;

public class DeanMenu {

    private final Dean dean;

    public DeanMenu(Dean dean) { this.dean = dean; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.dean");
            System.out.println("  " + UIStrings.get("msg.welcome") + dean.getName()
                    + "  |  School: " + dean.getSchool());
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.1.view.complaints"));
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.2.view.employee.requests"));
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.3.sign.employee.request"));
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.4.send.message"));
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.5.view.messages"));
            System.out.println(LanguageManager.get("ui.menus.DeanMenu.6.submit.official.request"));
            System.out.println(LanguageManager.get("common.schedule.menuItem", "7"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> viewComplaints();
                case 2 -> viewRequests();
                case 3 -> signRequest();
                case 4 -> sendMessage();
                case 5 -> viewMessages();
                case 6 -> submitRequest();
                case 7 -> ConsoleUtils.viewSemesterSchedule();
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void viewComplaints() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("dean.complaints.title", dean.getSchool())); ConsoleUtils.printLine();
        List<Complaint> complaints = dean.viewComplaints();
        if (complaints.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else {
            complaints.forEach(c -> {
                System.out.println("  " + c);
                String studentNames = c.getAboutStudents().stream()
                        .map(Student::getName)
                        .collect(Collectors.joining(", "));
                System.out.println(LanguageManager.get("dean.complaints.students", studentNames));
                ConsoleUtils.printLine();
            });
        }
        ConsoleUtils.pressEnter();
    }

    private void viewRequests() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.DeanMenu.employee.requests")); ConsoleUtils.printLine();
        List<EmployeeRequest> reqs = dean.viewAllRequests();
        if (reqs.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else {
            for (int i = 0; i < reqs.size(); i++)
                System.out.printf("  %d. %s%n", i + 1, reqs.get(i));
        }
        ConsoleUtils.pressEnter();
    }

    private void signRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.DeanMenu.sign.employee.request")); ConsoleUtils.printLine();
        List<EmployeeRequest> unsigned = dean.viewAllRequests().stream()
                .filter(r -> !r.isSignedByDean())
                .collect(java.util.stream.Collectors.toList());
        if (unsigned.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.DeanMenu.no.unsigned.requests")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < unsigned.size(); i++)
            System.out.printf("  %d. From: %s | Topic: %s%n",
                    i + 1, unsigned.get(i).getSender().getName(), unsigned.get(i).getTopic());
        int idx = ConsoleUtils.readInt("Select to sign (0=cancel): ");
        if (idx < 1 || idx > unsigned.size()) return;
        dean.signRequest(unsigned.get(idx - 1));
        Database.getInstance().save();
        ConsoleUtils.success("Request signed.");
        ConsoleUtils.pressEnter();
    }

    private void sendMessage() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendMessage.title")); ConsoleUtils.printLine();
        List<Employee> employees = Database.getInstance().getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(dean))
                .map(u -> (Employee) u)
                .collect(java.util.stream.Collectors.toList());
        for (int i = 0; i < employees.size(); i++)
            System.out.printf("  %d. %s (%s)%n", i + 1,
                    employees.get(i).getName(), employees.get(i).getClass().getSimpleName());
        int idx = ConsoleUtils.readInt("Select recipient (0=cancel): ");
        if (idx < 1 || idx > employees.size()) return;
        String text = ConsoleUtils.readLine("Message: ");
        dean.sendMessage(employees.get(idx - 1), text);
        Database.getInstance().save();
        ConsoleUtils.success("Message sent.");
        ConsoleUtils.pressEnter();
    }

    private void viewMessages() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.inbox.title")); ConsoleUtils.printLine();
        List<Message> msgs = dean.getUnreadMessages();
        if (msgs.isEmpty()) System.out.println(LanguageManager.get("common.inbox.empty"));
        else msgs.forEach(m -> System.out.println("  " + m));
        ConsoleUtils.pressEnter();
    }

    private void submitRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.DeanMenu.submit.official.request")); ConsoleUtils.printLine();
        String topic   = ConsoleUtils.readLine("Topic: ");
        String content = ConsoleUtils.readLine("Content: ");
        dean.submitRequest(topic, content);
        Database.getInstance().save();
        ConsoleUtils.success("Request submitted.");
        ConsoleUtils.pressEnter();
    }
}