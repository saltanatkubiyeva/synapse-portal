package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.LessonType;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.List;
import java.util.stream.Collectors;

public class StudentMenu {

    private final Student student;

    public StudentMenu(Student student) { this.student = student; }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.student");
            System.out.println("  " + UIStrings.get("msg.welcome") + student.getName()
                    + "  |  Credits: " + student.getSemesterCredits() + "/21"
                    + "  |  GPA: " + String.format("%.2f", student.getGpa()));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.1.view.available.courses"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.register.for.course.step.1"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.3.choose.lesson.slots.step.2.after.approval"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.4.my.schedule"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.5.view.my.marks"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.6.view.transcript"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.7.view.notifications"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.8.rate.a.teacher"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.9.view.news"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.10.subscribe.unsubscribe.journal"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.11.student.organizations"));
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.12.send.tech.request"));
            if (student instanceof GraduateStudent gs && gs.isTA())
                System.out.println(LanguageManager.get("ui.menus.StudentMenu.13.teaching.assistant.menu"));
            System.out.println(LanguageManager.get("common.schedule.menuItem", "14"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1  -> viewAvailableCourses();
                case 2  -> registerCourse();
                case 3  -> chooseSlots();
                case 4  -> viewSchedule();
                case 5  -> { student.viewMarks(); ConsoleUtils.pressEnter(); }
                case 6  -> { System.out.println(student.getTranscript()); ConsoleUtils.pressEnter(); }
                case 7  -> viewNotifications();
                case 8  -> rateTeacher();
                case 9  -> viewNews();
                case 10 -> manageJournals();
                case 11 -> manageOrganizations();
                case 12 -> sendTechRequest();
                case 13 -> {
                    if (student instanceof GraduateStudent gs && gs.isTA())
                        showTAMenu(gs.getTeachingAssistant());
                }
                case 14 -> ConsoleUtils.viewSemesterSchedule();
                case 0  -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void viewAvailableCourses() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("student.availableCourses.title", Database.getInstance().getCurrentSemester()));
        ConsoleUtils.printLine();
        List<CourseOffering> offerings = student.viewAvailableOfferings();
        if (offerings.isEmpty()) {
            System.out.println(UIStrings.get("msg.empty"));
        } else {
            System.out.printf("  %-4s %-8s %-30s %-5s %-10s %-8s %-15s%n",
                    "#", "Code", "Name", "Cr", "Type", "Spots", "HeadLecturer");
            ConsoleUtils.printLine();
            for (int i = 0; i < offerings.size(); i++) {
                CourseOffering o = offerings.get(i);
                System.out.printf("  %-4d %-8s %-30s %-5d %-10s %-8s %-15s%n",
                        i + 1,
                        o.getCourse().getCourseCode(),
                        o.getCourse().getName(),
                        o.getCourse().getCredits(),
                        o.getCourseTypeFor(student),
                        o.getEnrolledStudents().size() + "/" + o.getMaxStudents(),
                        o.getHeadLecturer() != null ? o.getHeadLecturer().getName() : "—");
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void registerCourse() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.course.registration.step.1"));
        ConsoleUtils.printLine();
        List<CourseOffering> offerings = student.viewAvailableOfferings();
        if (offerings.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.courses.available.for.registration"));
            ConsoleUtils.pressEnter(); return;
        }
        System.out.printf("  %-4s %-8s %-30s %-5s %-10s%n", "#", "Code", "Name", "Cr", "Spots");
        ConsoleUtils.printLine();
        for (int i = 0; i < offerings.size(); i++) {
            CourseOffering o = offerings.get(i);
            System.out.printf("  %-4d %-8s %-30s %-5d %-10s%n",
                    i + 1, o.getCourse().getCourseCode(), o.getCourse().getName(),
                    o.getCourse().getCredits(),
                    o.getEnrolledStudents().size() + "/" + o.getMaxStudents());
        }
        ConsoleUtils.printLine();
        int idx = ConsoleUtils.readInt("Select course (0=cancel): ");
        if (idx < 1 || idx > offerings.size()) return;

        CourseOffering chosen = offerings.get(idx - 1);
        System.out.println(LanguageManager.get("student.register.course", chosen.getCourse().getName()));
        System.out.println(LanguageManager.get("student.register.credits", chosen.getCourse().getCredits()));
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.your.application.will.be.sent.to.or.manager.for.ap"));
        String confirm = ConsoleUtils.readLine("Confirm? (yes/no): ");
        if (!confirm.equalsIgnoreCase("yes")) return;

        try {
            student.registerForOffering(chosen);
            Database.getInstance().save();
            ConsoleUtils.success("Application submitted! Status: PENDING");
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.wait.for.or.manager.approval.then.choose.your.slot"));
        } catch (Exception e) {
            ConsoleUtils.error(e.getMessage());
        }
        ConsoleUtils.pressEnter();
    }

    private void chooseSlots() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.choose.lesson.slots.step.2"));
        ConsoleUtils.printLine();

        List<CourseOffering> approved = student.getEnrolledOfferings().stream()
                .filter(o -> !student.slotsComplete(o))
                .collect(Collectors.toList());

        if (approved.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.approved.courses.awaiting.slot.selection"));
            ConsoleUtils.pressEnter(); return;
        }

        System.out.printf("  %-4s %-30s %-10s %-10s%n", "#", "Course", "Lectures", "Practices");
        ConsoleUtils.printLine();
        for (int i = 0; i < approved.size(); i++) {
            CourseOffering o = approved.get(i);
            long lChosen = student.getChosenSlots(o).stream().filter(s -> s.getType() == LessonType.LECTURE).count();
            long pChosen = student.getChosenSlots(o).stream().filter(s -> s.getType() == LessonType.PRACTICE).count();
            System.out.printf("  %-4d %-30s %d/%d       %d/%d%n",
                    i + 1, o.getCourse().getName(),
                    lChosen, o.getCourse().getLecturesPerWeek(),
                    pChosen, o.getCourse().getPracticesPerWeek());
        }

        int idx = ConsoleUtils.readInt("Select course (0=cancel): ");
        if (idx < 1 || idx > approved.size()) return;
        selectSlotsForOffering(approved.get(idx - 1));
    }

    private void selectSlotsForOffering(CourseOffering offering) {
        while (!student.slotsComplete(offering)) {
            ConsoleUtils.clearScreen();
            System.out.println(LanguageManager.get("student.slots.for", offering.getCourse().getName()));
            long lChosen = student.getChosenSlots(offering).stream().filter(s -> s.getType() == LessonType.LECTURE).count();
            long pChosen = student.getChosenSlots(offering).stream().filter(s -> s.getType() == LessonType.PRACTICE).count();
            System.out.println(LanguageManager.get("student.slots.progress", lChosen, offering.getCourse().getLecturesPerWeek(), pChosen, offering.getCourse().getPracticesPerWeek()));
            ConsoleUtils.printLine();

            List<LessonSlot> available = offering.getSlots().stream()
                    .filter(s -> !student.getChosenSlots(offering).contains(s))
                    .filter(LessonSlot::hasSpots)
                    .collect(Collectors.toList());

            if (available.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.available.slots")); ConsoleUtils.pressEnter(); return; }

            System.out.printf("  %-4s %-10s %-10s %-8s %-10s %-25s %-8s%n",
                    "#", "Type", "Day", "Start", "End", "Teacher", "Spots");
            ConsoleUtils.printLine();
            for (int i = 0; i < available.size(); i++) {
                LessonSlot s = available.get(i);
                System.out.printf("  %-4d %-10s %-10s %-8s %-10s %-25s %d/%d%n",
                        i + 1, s.getType(), s.getDayOfWeek(),
                        s.getStartTime(), s.getEndTime(),
                        s.getTeacher().getName(), s.getCurrentStudents(), s.getMaxStudents());
            }
            ConsoleUtils.printLine();

            int idx = ConsoleUtils.readInt("Choose slot (0=cancel): ");
            if (idx == 0) return;
            if (idx < 1 || idx > available.size()) { System.out.println(UIStrings.get("msg.invalid")); continue; }

            try {
                student.chooseSlot(offering, available.get(idx - 1));
                if (student.slotsComplete(offering)) {
                    ConsoleUtils.success("All slots selected! Status: REGISTERED. Credits added: " + offering.getCourse().getCredits());
                    Database.getInstance().save();
                } else {
                    ConsoleUtils.success("Slot added. Continue selecting...");
                }
            } catch (Exception e) {
                ConsoleUtils.error(e.getMessage());
                ConsoleUtils.pressEnter();
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void viewSchedule() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.my.schedule"));
        ConsoleUtils.printLine();
        List<LessonSlot> slots = student.getAllChosenSlots();
        if (slots.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.slots.chosen.yet"));
        } else {
            slots.stream()
                    .sorted(java.util.Comparator
                            .comparing(LessonSlot::getDayOfWeek)
                            .thenComparing(LessonSlot::getStartTime))
                    .forEach(s -> System.out.printf("  %-10s %-8s %-8s | %-10s | %-8s | %s%n",
                            s.getDayOfWeek(), s.getStartTime(), s.getEndTime(),
                            s.getType(), s.getRoom(), s.getTeacher().getName()));
        }
        ConsoleUtils.pressEnter();
    }

    private void viewNotifications() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.notifications.title"));
        ConsoleUtils.printLine();
        List<Notification> notifs = student.getUnreadNotifications();
        if (notifs.isEmpty()) System.out.println(LanguageManager.get("common.notifications.empty"));
        else { notifs.forEach(n -> System.out.println("  " + n)); student.markAllNotificationsRead(); }
        ConsoleUtils.pressEnter();
    }

    private void rateTeacher() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.rate.a.teacher")); ConsoleUtils.printLine();
        List<LessonSlot> slots = student.getAllChosenSlots();
        if (slots.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.teachers.to.rate.yet")); ConsoleUtils.pressEnter(); return; }

        List<Teacher> teachers = slots.stream()
                .map(LessonSlot::getTeacher).distinct().collect(Collectors.toList());

        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            System.out.printf("  %d. %-25s (rating: %.1f) %s%n",
                    i + 1, t.getName(), t.getRating(),
                    student.hasRated(t) ? "[already rated]" : "");
        }

        int idx = ConsoleUtils.readInt("Select teacher (0=cancel): ");
        if (idx < 1 || idx > teachers.size()) return;
        int rating = ConsoleUtils.readInt("Rating (1-5): ");
        try {
            student.rateTeacher(teachers.get(idx - 1), rating);
            Database.getInstance().save();
            ConsoleUtils.success("Rating submitted.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void viewNews() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("common.newsFeed.title"));
        ConsoleUtils.printLine();
        List<News> news = Database.getInstance().getNewsList();
        if (news.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); }
        else {
            news.stream()
                    .sorted((a, b) -> Boolean.compare(b.isPinned(), a.isPinned()))
                    .forEach(n -> System.out.println((n.isPinned() ? "  📌 " : "     ") + n));
            ConsoleUtils.printLine();
            int idx = ConsoleUtils.readInt("Select news to comment (0=skip): ");
            if (idx >= 1 && idx <= news.size()) {
                News selected = news.stream()
                        .sorted((a, b) -> Boolean.compare(b.isPinned(), a.isPinned()))
                        .collect(Collectors.toList()).get(idx - 1);
                if (!selected.getComments().isEmpty()) {
                    System.out.println(LanguageManager.get("ui.menus.StudentMenu.comments"));
                    selected.getComments().forEach(c -> System.out.println("    > " + c));
                }
                String comment = ConsoleUtils.readLine("Add comment (Enter to skip): ");
                if (!comment.isEmpty()) {
                    selected.addComment(student.getName() + ": " + comment);
                    Database.getInstance().save();
                    ConsoleUtils.success("Comment added.");
                }
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void manageJournals() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.journals")); ConsoleUtils.printLine();
        List<Journal> journals = Database.getInstance().getJournals();
        if (journals.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < journals.size(); i++) {
            Journal j = journals.get(i);
            boolean subscribed = j.getSubscriberIds().contains(student.getId());
            System.out.printf("  %d. %-30s [%s]%n", i + 1, j.getName(),
                    subscribed ? "SUBSCRIBED" : "not subscribed");
        }
        int idx = ConsoleUtils.readInt("Select journal to toggle (0=cancel): ");
        if (idx < 1 || idx > journals.size()) return;
        Journal j = journals.get(idx - 1);
        if (j.getSubscriberIds().contains(student.getId())) {
            student.unsubscribeFromJournal(j);
            ConsoleUtils.success("Unsubscribed from " + j.getName());
        } else {
            student.subscribeToJournal(j);
            ConsoleUtils.success("Subscribed to " + j.getName());
        }
        Database.getInstance().save();
        ConsoleUtils.pressEnter();
    }

    private void manageOrganizations() {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println(LanguageManager.get("ui.menus.ORManagerMenu.student.organizations")); ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.1.browse.request.to.join"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.my.organizations"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.3.propose.new.organization"));
            System.out.println(LanguageManager.get("common.back"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> browseAndJoin();
                case 2 -> myOrganizations();
                case 3 -> proposeOrganization();
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void browseAndJoin() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.all.organizations")); ConsoleUtils.printLine();
        List<StudentOrganization> orgs = Database.getInstance().getOrganizations();
        if (orgs.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < orgs.size(); i++) {
            StudentOrganization o = orgs.get(i);
            String status;
            if (o.isMember(student)) status = "MEMBER";
            else if (o.hasPendingRequest(student)) status = "REQUEST PENDING";
            else status = "—";
            System.out.printf("  %d. %-30s members: %-3d head: %-20s [%s]%n",
                    i + 1, o.getName(), o.getMembers().size(),
                    o.getHead() != null ? o.getHead().getName() : "—", status);
        }
        int idx = ConsoleUtils.readInt("Select organization to request join (0=cancel): ");
        if (idx < 1 || idx > orgs.size()) return;
        StudentOrganization org = orgs.get(idx - 1);
        try {
            student.requestJoinOrganization(org);
            Database.getInstance().save();
            ConsoleUtils.success("Join request sent! The head will review it.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void myOrganizations() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.my.organizations")); ConsoleUtils.printLine();
        List<StudentOrganization> myOrgs = Database.getInstance().getOrganizations().stream()
                .filter(o -> o.isMember(student)).collect(Collectors.toList());
        if (myOrgs.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.you.are.not.in.any.organization")); ConsoleUtils.pressEnter(); return; }

        for (int i = 0; i < myOrgs.size(); i++) {
            StudentOrganization o = myOrgs.get(i);
            boolean isHead = o.getHead() != null && o.getHead().equals(student);
            System.out.printf("  %d. %-30s [%s]%n", i + 1, o.getName(), isHead ? "HEAD" : "member");
        }
        int idx = ConsoleUtils.readInt("Select organization (0=back): ");
        if (idx < 1 || idx > myOrgs.size()) return;
        StudentOrganization org = myOrgs.get(idx - 1);
        boolean isHead = org.getHead() != null && org.getHead().equals(student);
        orgActionMenu(org, isHead);
    }

    private void orgActionMenu(StudentOrganization org, boolean isHead) {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println(LanguageManager.get("student.org.name", org.getName())); ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ormanager.org.description", org.getDescription()));
            System.out.println(LanguageManager.get("student.org.membersHead", org.getMembers().size(), org.getHead() != null ? org.getHead().getName() : "—"));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.1.view.members"));
            if (isHead) {
                System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.review.join.requests"));
                System.out.println(LanguageManager.get("ui.menus.StudentMenu.3.transfer.leadership"));
                System.out.println(LanguageManager.get("ui.menus.StudentMenu.4.request.dissolution.via.ormanager"));
            } else {
                System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.leave.organization"));
            }
            System.out.println(LanguageManager.get("common.back"));
            ConsoleUtils.printLine();

            int choice = ConsoleUtils.readInt(UIStrings.get("prompt.choice"));
            if (isHead) {
                switch (choice) {
                    case 1 -> viewOrgMembers(org);
                    case 2 -> reviewJoinRequests(org);
                    case 3 -> transferLeadership(org);
                    case 4 -> { ConsoleUtils.success("Contact ORManager to dissolve the organization."); ConsoleUtils.pressEnter(); }
                    case 0 -> { return; }
                    default -> System.out.println(UIStrings.get("msg.invalid"));
                }
            } else {
                switch (choice) {
                    case 1 -> viewOrgMembers(org);
                    case 2 -> leaveOrganization(org);
                    case 0 -> { return; }
                    default -> System.out.println(UIStrings.get("msg.invalid"));
                }
            }
        }
    }

    private void viewOrgMembers(StudentOrganization org) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("student.org.members.title", org.getName())); ConsoleUtils.printLine();
        org.getMembers().forEach(m -> System.out.printf("  %-30s %s%n",
                m.getName(), m.equals(org.getHead()) ? "[HEAD]" : ""));
        ConsoleUtils.pressEnter();
    }

    private void reviewJoinRequests(StudentOrganization org) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("student.org.joinRequests.title", org.getName())); ConsoleUtils.printLine();
        List<Student> requests = org.getJoinRequests();
        if (requests.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.pending.requests")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < requests.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, requests.get(i).getName());
        int idx = ConsoleUtils.readInt("Select request (0=cancel): ");
        if (idx < 1 || idx > requests.size()) return;
        Student applicant = requests.get(idx - 1);
        System.out.println(LanguageManager.get("common.approveRejectCancel"));
        int action = ConsoleUtils.readInt("Choice: ");
        try {
            if (action == 1) {
                org.approveJoin(applicant);
                applicant.addToOrganization(org);
                ConsoleUtils.success(applicant.getName() + " approved.");
            } else if (action == 2) {
                org.rejectJoin(applicant);
                ConsoleUtils.success(applicant.getName() + " rejected.");
            }
            Database.getInstance().save();
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void transferLeadership(StudentOrganization org) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("student.org.transfer.title", org.getName())); ConsoleUtils.printLine();
        List<Student> members = org.getMembers().stream()
                .filter(m -> !m.equals(student)).collect(Collectors.toList());
        if (members.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.other.members.to.transfer.to")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < members.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, members.get(i).getName());
        int idx = ConsoleUtils.readInt("Select new head (0=cancel): ");
        if (idx < 1 || idx > members.size()) return;
        try {
            student.transferLeadership(org, members.get(idx - 1));
            Database.getInstance().save();
            ConsoleUtils.success("Leadership transferred to " + members.get(idx - 1).getName());
            return;
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void leaveOrganization(StudentOrganization org) {
        System.out.println(LanguageManager.get("student.org.leave.confirm", org.getName()));
        if (!ConsoleUtils.readLine("Confirm: ").equalsIgnoreCase("yes")) return;
        try {
            student.leaveOrganization(org);
            Database.getInstance().save();
            ConsoleUtils.success("You left " + org.getName());
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void proposeOrganization() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.propose.new.organization")); ConsoleUtils.printLine();
        String name = ConsoleUtils.readLine("Organization name (0=cancel): ");
        if (name.equals("0")) return;
        String desc = ConsoleUtils.readLine("Description: ");
        student.proposeOrganization(name, desc);
        Database.getInstance().save();
        ConsoleUtils.success("Proposal submitted! OR Manager will review it.");
        ConsoleUtils.pressEnter();
    }

    private void showTAMenu(TeachingAssistant ta) {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.teaching.assistant.menu"));
            System.out.println(LanguageManager.get("student.ta.assisting", ta.getAssistedTeacher().getName()));
            System.out.println(LanguageManager.get("student.ta.course", ta.getOffering().getCourse().getName(), ta.getOffering().getSemester()));
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.1.view.my.accessible.slots"));
            System.out.println(LanguageManager.get("ui.menus.StudentMenu.2.add.lesson.score.for.student"));
            System.out.println(LanguageManager.get("common.back"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> viewTASlots(ta);
                case 2 -> addTAScore(ta);
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void viewTASlots(TeachingAssistant ta) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.my.accessible.slots")); ConsoleUtils.printLine();
        List<LessonSlot> slots = ta.getAccessibleSlots();
        if (slots.isEmpty()) System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.slots"));
        else slots.forEach(s -> System.out.printf("  %-10s %-10s %-8s %-8s %-8s%n",
                s.getType(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), s.getRoom()));
        ConsoleUtils.pressEnter();
    }

    private void addTAScore(TeachingAssistant ta) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.StudentMenu.add.lesson.score.ta")); ConsoleUtils.printLine();
        List<Student> students = new java.util.ArrayList<>(ta.getOffering().getEnrolledStudents());
        if (students.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.StudentMenu.no.students.enrolled")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < students.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, students.get(i).getName());
        int sIdx = ConsoleUtils.readInt("Select student (0=cancel): ");
        if (sIdx == 0 || sIdx > students.size()) return;
        Student target = students.get(sIdx - 1);

        List<LessonSlot> slots = ta.getAccessibleSlots();
        for (int i = 0; i < slots.size(); i++)
            System.out.printf("  %d. %s — %s %s-%s%n", i + 1,
                    slots.get(i).getType(), slots.get(i).getDayOfWeek(),
                    slots.get(i).getStartTime(), slots.get(i).getEndTime());
        int slotIdx = ConsoleUtils.readInt("Select slot (0=cancel): ");
        if (slotIdx == 0 || slotIdx > slots.size()) return;

        System.out.println(LanguageManager.get("common.periodPrompt"));
        kz.synapse.enums.AttestationPeriod period = ConsoleUtils.readInt("Choice: ") == 1
                ? kz.synapse.enums.AttestationPeriod.ATT1 : kz.synapse.enums.AttestationPeriod.ATT2;
        double score = ConsoleUtils.readDouble("Score: ");
        String comment = ConsoleUtils.readLine("Comment (Enter to skip): ");
        try {
            ta.addScore(target, java.time.LocalDate.now(),
                    slots.get(slotIdx - 1).getType(), period, score,
                    comment.isEmpty() ? "" : comment);
            Database.getInstance().save();
            Mark m = ta.getOffering().getMark(target);
            ConsoleUtils.success("Score added. ATT1: " + m.getAtt1() + "  ATT2: " + m.getAtt2());
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void sendTechRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendTechRequest.title")); ConsoleUtils.printLine();
        String desc = ConsoleUtils.readLine("Description (0=cancel): ");
        if (desc.equals("0")) return;
        student.sendTechRequest(desc);
        Database.getInstance().save();
        ConsoleUtils.success("Tech request submitted.");
        ConsoleUtils.pressEnter();
    }
}