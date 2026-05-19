package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.RequestStatus;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.List;

public class TechSupportMenu {

    private final TechSupportSpecialist tech;

    public TechSupportMenu(TechSupportSpecialist tech) { this.tech = tech; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.techsupport");
            System.out.println("  " + UIStrings.get("msg.welcome") + tech.getName());
            long newCount = Database.getInstance().getTechRequests().stream()
                    .filter(r -> r.getStatus() == RequestStatus.NEW).count();
            if (newCount > 0)
                System.out.println(LanguageManager.get("techsupport.newRequests", newCount));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.1.view.all.requests"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.2.view.new.requests"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.3.view.accepted.requests"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.4.accept.request"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.5.reject.request"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.6.mark.as.done"));
            System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.7.send.message"));
            System.out.println(LanguageManager.get("ui.menus.TeacherMenu.8.view.messages"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> viewRequests(tech.viewRequests(), "ALL REQUESTS");
                case 2 -> viewRequests(tech.getByStatus(RequestStatus.NEW), "NEW REQUESTS");
                case 3 -> viewRequests(tech.getByStatus(RequestStatus.ACCEPTED), "ACCEPTED REQUESTS");
                case 4 -> changeStatus(RequestStatus.ACCEPTED);
                case 5 -> changeStatus(RequestStatus.REJECTED);
                case 6 -> changeStatus(RequestStatus.DONE);
                case 7 -> sendMessage();
                case 8 -> viewMessages();
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void viewRequests(List<Request> requests, String title) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println("  " + title); ConsoleUtils.printLine();
        if (requests.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else {
            System.out.printf("  %-4s %-12s %-20s %-35s%n", "#", "Status", "From", "Description");
            ConsoleUtils.printLine();
            for (int i = 0; i < requests.size(); i++)
                System.out.printf("  %-4d %-12s %-20s %-35s%n",
                        i + 1, requests.get(i).getStatus(),
                        requests.get(i).getRequesterName(),
                        requests.get(i).getDescription());
        }
        ConsoleUtils.pressEnter();
    }

    private void changeStatus(RequestStatus newStatus) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("techsupport.requestStatus", newStatus)); ConsoleUtils.printLine();
        List<Request> all = tech.viewRequests();
        // фильтруем по логике: accept/reject → VIEWED, done → ACCEPTED
        List<Request> eligible = all.stream()
                .filter(r -> {
                    if (newStatus == RequestStatus.ACCEPTED || newStatus == RequestStatus.REJECTED)
                        return r.getStatus() == RequestStatus.VIEWED;
                    if (newStatus == RequestStatus.DONE)
                        return r.getStatus() == RequestStatus.ACCEPTED;
                    return false;
                })
                .collect(java.util.stream.Collectors.toList());

        if (eligible.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.TechSupportMenu.no.eligible.requests")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < eligible.size(); i++)
            System.out.printf("  %d. [%s] %s — %s%n", i + 1,
                    eligible.get(i).getStatus(),
                    eligible.get(i).getRequesterName(),
                    eligible.get(i).getDescription());
        int idx = ConsoleUtils.readInt("Select (0=cancel): ");
        if (idx < 1 || idx > eligible.size()) return;
        Request r = eligible.get(idx - 1);
        switch (newStatus) {
            case ACCEPTED -> tech.accept(r);
            case REJECTED -> tech.reject(r);
            case DONE     -> tech.markDone(r);
            default -> {}
        }
        Database.getInstance().save();
        ConsoleUtils.success("Status updated to " + newStatus);
        ConsoleUtils.pressEnter();
    }

    private void sendMessage() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendMessage.title")); ConsoleUtils.printLine();
        List<Employee> employees = Database.getInstance().getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(tech))
                .map(u -> (Employee) u)
                .collect(java.util.stream.Collectors.toList());
        for (int i = 0; i < employees.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, employees.get(i).getName());
        int idx = ConsoleUtils.readInt("Select recipient (0=cancel): ");
        if (idx < 1 || idx > employees.size()) return;
        String text = ConsoleUtils.readLine("Message: ");
        tech.sendMessage(employees.get(idx - 1), text);
        Database.getInstance().save();
        ConsoleUtils.success("Message sent.");
        ConsoleUtils.pressEnter();
    }

    private void viewMessages() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.inbox.title")); ConsoleUtils.printLine();
        List<Message> msgs = tech.getUnreadMessages();
        if (msgs.isEmpty()) System.out.println(LanguageManager.get("common.inbox.empty"));
        else msgs.forEach(m -> System.out.println("  " + m));
        ConsoleUtils.pressEnter();
    }
}
