package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.SemesterType;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.Comparator;
import java.util.List;

public class ORManagerMenu {

    private final ORManager manager;

    public ORManagerMenu(ORManager manager) { this.manager = manager; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.ormanager");
            System.out.println("  " + UIStrings.get("msg.welcome") + manager.getName());
            String sem = Database.getInstance().getCurrentSemester() != null
                    ? Database.getInstance().getCurrentSemester().toString() : "None";
            System.out.println(LanguageManager.get("ormanager.header", sem, Database.getInstance().isRegistrationOpen() ? "OPEN" : "CLOSED"));
            long pendingOrgs = Database.getInstance().getOrgProposals().stream()
                    .filter(p -> p.getStatus() == OrganizationProposal.Status.PENDING).count();
            if (pendingOrgs > 0) System.out.println(LanguageManager.get("ormanager.orgPending", pendingOrgs));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.1.open.semester"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.2.close.semester"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.3.view.pending.registrations"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.4.approve.registration"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.5.reject.registration"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.6.view.all.students"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.7.view.all.teachers"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.8.performance.report"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.9.view.signed.employee.requests"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.10.publish.university.news"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.11.manage.student.organizations"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.12.send.tech.request"));
            System.out.println(LanguageManager.get("common.schedule.menuItem", "13"));
            System.out.println("  14. Remove Course Offering");
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1  -> openSemester();
                case 2  -> closeSemester();
                case 3  -> viewPending();
                case 4  -> approveRegistration();
                case 5  -> rejectRegistration();
                case 6  -> viewStudents();
                case 7  -> viewTeachers();
                case 8  -> { System.out.println(manager.createPerformanceReport()); ConsoleUtils.pressEnter(); }
                case 9  -> viewSignedRequests();
                case 10 -> publishNews();
                case 11 -> manageOrganizations();
                case 12 -> sendTechRequest();
                case 13 -> ConsoleUtils.viewSemesterSchedule();
                case 14 -> removeCourseOffering();
                case 0  -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void openSemester() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.open.semester")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.1.fall.2.spring.3.summer.0.cancel"));
        int choice = ConsoleUtils.readInt("Choice: ");
        if (choice == 0) return;
        SemesterType sem = switch (choice) {
            case 1 -> SemesterType.FALL;
            case 2 -> SemesterType.SPRING;
            default -> SemesterType.SUMMER;
        };
        manager.openSemester(sem);
        Database.getInstance().save();
        ConsoleUtils.success("Semester " + sem + " opened. Registration is OPEN.");
        ConsoleUtils.pressEnter();
    }

    private void closeSemester() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.close.semester")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.warning.this.will.finalize.all.marks.and.update.tr"));
        String confirm = ConsoleUtils.readLine("Type 'confirm' to proceed (or Enter to cancel): ");
        if (!confirm.equals("confirm")) { System.out.println(LanguageManager.get("common.cancelled")); ConsoleUtils.pressEnter(); return; }
        try {
            manager.closeSemester();
            Database.getInstance().save();
            ConsoleUtils.success("Semester closed. All transcripts updated.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void viewPending() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.pending.registrations")); ConsoleUtils.printLine();
        List<CourseRegistration> pending = manager.viewPendingRegistrations();
        if (pending.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.pending.registrations")); }
        else {
            System.out.printf("  %-4s %-25s %-30s %-5s%n", "#", "Student", "Course", "Cr");
            ConsoleUtils.printLine();
            for (int i = 0; i < pending.size(); i++) {
                CourseRegistration r = pending.get(i);
                System.out.printf("  %-4d %-25s %-30s %-5d%n", i + 1,
                        r.getStudent().getName(),
                        r.getOffering().getCourse().getName(),
                        r.getOffering().getCourse().getCredits());
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void approveRegistration() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.approve.registration")); ConsoleUtils.printLine();
        List<CourseRegistration> pending = manager.viewPendingRegistrations();
        if (pending.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.pending.registrations")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < pending.size(); i++)
            System.out.printf("  %d. %s → %s [%d cr]%n", i + 1,
                    pending.get(i).getStudent().getName(),
                    pending.get(i).getOffering().getCourse().getName(),
                    pending.get(i).getOffering().getCourse().getCredits());
        System.out.println(LanguageManager.get("common.cancel"));
        int idx = ConsoleUtils.readInt("Select to approve: ");
        if (idx == 0 || idx > pending.size()) return;
        try {
            manager.approveRegistration(pending.get(idx - 1));
            Database.getInstance().save();
            ConsoleUtils.success("Registration APPROVED. Student can now choose slots.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void rejectRegistration() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.reject.registration")); ConsoleUtils.printLine();
        List<CourseRegistration> pending = manager.viewPendingRegistrations();
        if (pending.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.pending.registrations")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < pending.size(); i++)
            System.out.printf("  %d. %s → %s%n", i + 1,
                    pending.get(i).getStudent().getName(),
                    pending.get(i).getOffering().getCourse().getName());
        System.out.println(LanguageManager.get("common.cancel"));
        int idx = ConsoleUtils.readInt("Select to reject: ");
        if (idx == 0 || idx > pending.size()) return;
        manager.rejectRegistration(pending.get(idx - 1));
        Database.getInstance().save();
        ConsoleUtils.success("Registration REJECTED.");
        ConsoleUtils.pressEnter();
    }

    private void viewStudents() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.all.students")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.sort.1.by.gpa.2.alphabetically.0.cancel"));
        int s = ConsoleUtils.readInt("Choice: ");
        if (s == 0) return;
        Comparator<Student> comp = s == 1 ? Comparator.reverseOrder() : Comparator.comparing(Student::getName);
        List<Student> students = manager.viewStudents(comp);
        if (students.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); }
        else {
            System.out.printf("  %-4s %-25s %-10s %-6s %-8s%n", "#", "Name", "School", "GPA", "Credits");
            ConsoleUtils.printLine();
            for (int i = 0; i < students.size(); i++)
                System.out.printf("  %-4d %-25s %-10s %-6.2f %-8d%n", i + 1,
                        students.get(i).getName(), students.get(i).getSchool(),
                        students.get(i).getGpa(), students.get(i).getSemesterCredits());
        }
        ConsoleUtils.pressEnter();
    }

    private void viewTeachers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.all.teachers")); ConsoleUtils.printLine();
        List<Teacher> teachers = manager.viewTeachers(Comparator.naturalOrder());
        if (teachers.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); }
        else teachers.forEach(t -> System.out.printf("  %-25s %-15s %-10s %.1f⭐%n",
                t.getName(), t.getPosition(), t.getSchool(), t.getRating()));
        ConsoleUtils.pressEnter();
    }

    private void viewSignedRequests() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.signed.employee.requests")); ConsoleUtils.printLine();
        List<EmployeeRequest> reqs = manager.viewSignedEmployeeRequests();
        if (reqs.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else reqs.forEach(r -> System.out.println("  " + r));
        ConsoleUtils.pressEnter();
    }

    private void publishNews() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.publish.news")); ConsoleUtils.printLine();
        String title = ConsoleUtils.readLine("Title (0=cancel): ");
        if (title.equals("0")) return;
        String content = ConsoleUtils.readLine("Content: ");
        manager.addNews(title, content, kz.synapse.enums.NewsType.ANNOUNCEMENT);
        Database.getInstance().save();
        ConsoleUtils.success("News published.");
        ConsoleUtils.pressEnter();
    }

    private void manageOrganizations() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.student.organizations")); ConsoleUtils.printLine();
            List<StudentOrganization> orgs = Database.getInstance().getOrganizations();
            if (orgs.isEmpty()) System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.organizations.yet"));
            else for (int i = 0; i < orgs.size(); i++)
                System.out.printf("  %d. %-30s | members: %d | head: %s%n",
                        i + 1, orgs.get(i).getName(), orgs.get(i).getMembers().size(),
                        orgs.get(i).getHead() != null ? orgs.get(i).getHead().getName() : "—");
            ConsoleUtils.printLine();

            long pending = Database.getInstance().getOrgProposals().stream()
                    .filter(p -> p.getStatus() == OrganizationProposal.Status.PENDING).count();
            System.out.println(LanguageManager.get("ormanager.proposals.review", pending));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.2.view.organization.details"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.3.dissolve.organization.administrative"));
            System.out.println(LanguageManager.get("common.back"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> reviewProposals();
                case 2 -> viewOrgDetails(orgs);
                case 3 -> dissolveOrganization(orgs);
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void reviewProposals() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.organization.proposals")); ConsoleUtils.printLine();
        List<OrganizationProposal> proposals = manager.getPendingProposals();
        if (proposals.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.pending.proposals")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < proposals.size(); i++)
            System.out.printf("  %d. %-30s | Proposer: %-20s | Desc: %s%n",
                    i + 1, proposals.get(i).getName(),
                    proposals.get(i).getProposer().getName(),
                    proposals.get(i).getDescription());
        System.out.println(LanguageManager.get("common.back"));
        int idx = ConsoleUtils.readInt("Select proposal: ");
        if (idx == 0 || idx > proposals.size()) return;
        OrganizationProposal proposal = proposals.get(idx - 1);
        System.out.println(LanguageManager.get("common.approveRejectCancel"));
        int action = ConsoleUtils.readInt("Choice: ");
        if (action == 1) {
            StudentOrganization org = manager.approveProposal(proposal);
            Database.getInstance().save();
            ConsoleUtils.success("Organization '" + org.getName() + "' created! "
                    + proposal.getProposer().getName() + " is Head.");
        } else if (action == 2) {
            manager.rejectProposal(proposal);
            Database.getInstance().save();
            ConsoleUtils.success("Proposal rejected.");
        }
        ConsoleUtils.pressEnter();
    }

    private void viewOrgDetails(List<StudentOrganization> orgs) {
        if (orgs.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.organizations")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < orgs.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, orgs.get(i).getName());
        int idx = ConsoleUtils.readInt("Select organization: ");
        if (idx < 1 || idx > orgs.size()) return;
        StudentOrganization org = orgs.get(idx - 1);
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ormanager.org.name", org.getName())); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ormanager.org.description", org.getDescription()));
        System.out.println(LanguageManager.get("ormanager.org.head", org.getHead() != null ? org.getHead().getName() : "—"));
        System.out.println(LanguageManager.get("ormanager.org.members", org.getMembers().size()));
        org.getMembers().forEach(m -> System.out.printf("    %-30s %s%n",
                m.getName(), m.equals(org.getHead()) ? "[HEAD]" : ""));
        if (!org.getJoinRequests().isEmpty()) {
            System.out.println(LanguageManager.get("ormanager.org.pendingJoins", org.getJoinRequests().size()));
        }
        ConsoleUtils.pressEnter();
    }

    private void dissolveOrganization(List<StudentOrganization> orgs) {
        if (orgs.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.no.organizations")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < orgs.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, orgs.get(i).getName());
        System.out.println(LanguageManager.get("common.cancel"));
        int idx = ConsoleUtils.readInt("Select to dissolve: ");
        if (idx == 0 || idx > orgs.size()) return;
        StudentOrganization org = orgs.get(idx - 1);
        System.out.println(LanguageManager.get("ormanager.dissolve.warning",
                org.getName(), org.getMembers().size()));
        String confirm = ConsoleUtils.readLine("Type 'confirm' to proceed: ");
        if (!confirm.equals("confirm")) { System.out.println(LanguageManager.get("common.cancelled")); ConsoleUtils.pressEnter(); return; }
        manager.dissolveOrganization(org);
        Database.getInstance().save();
        ConsoleUtils.success("Organization dissolved.");
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

    private void removeCourseOffering() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println("  REMOVE COURSE OFFERING");
        ConsoleUtils.printLine();

        java.util.List<CourseOffering> all =
                new java.util.ArrayList<>(Database.getInstance().getCourseOfferings());
        if (all.isEmpty()) {
            System.out.println("  No offerings found.");
            ConsoleUtils.pressEnter(); return;
        }

        for (int i = 0; i < all.size(); i++) {
            CourseOffering o = all.get(i);
            boolean published = Database.getInstance()
                    .getPublishedOfferings().contains(o);
            System.out.printf("  %d. %-8s %-35s [%s] %s | students: %d%n",
                    i + 1,
                    o.getCourse().getCourseCode(),
                    o.getCourse().getName(),
                    o.getSemester(),
                    published ? "[PUBLISHED]" : "[draft]",
                    o.getEnrolledStudents().size());
        }
        System.out.println("  0. Cancel");
        ConsoleUtils.printLine();

        int idx = ConsoleUtils.readInt("Select offering to remove: ");
        if (idx == 0 || idx > all.size()) return;

        CourseOffering target = all.get(idx - 1);

        if (!target.getEnrolledStudents().isEmpty()) {
            System.out.printf("  WARNING: %d student(s) enrolled. Remove anyway? (yes/no): ",
                    target.getEnrolledStudents().size());
            String confirm = ConsoleUtils.readLine("");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("  Cancelled.");
                ConsoleUtils.pressEnter(); return;
            }
        }

        Database.getInstance().removeCourseOffering(target);
        Database.getInstance().save();
        ConsoleUtils.success("Offering removed: "
                + target.getCourse().getName()
                + " [" + target.getSemester() + "]");
        ConsoleUtils.pressEnter();
    }

}