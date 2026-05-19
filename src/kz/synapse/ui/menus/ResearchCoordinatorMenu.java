package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.CitationFormat;
import kz.synapse.enums.School;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResearchCoordinatorMenu {

    private final ResearchCoordinator coordinator;

    public ResearchCoordinatorMenu(ResearchCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.research");
            System.out.println("  " + UIStrings.get("msg.welcome") + coordinator.getName()
                    + "  |  Researchers: " + coordinator.getAllResearchers().size()
                    + "  |  Projects: " + coordinator.getAllProjects().size());
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.1.make.user.a.researcher"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.2.view.all.researchers"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.3.create.research.project"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.4.view.all.projects"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.5.print.all.papers.sorted"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.6.top.cited.researcher.university"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.7.top.cited.by.school"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.8.top.cited.by.year"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.9.announce.top.researcher"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.10.update.paper.citations"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.11.add.journal.to.system"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.12.manage.supervisors"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.13.add.paper.to.research.project"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.14.send.message"));
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.15.send.tech.request"));
            System.out.println("  " + UIStrings.get("msg.logout"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1  -> makeResearcher();
                case 2  -> viewResearchers();
                case 3  -> createProject();
                case 4  -> viewProjects();
                case 5  -> printPapers();
                case 6  -> showTop(coordinator.getTopCitedResearcher(), "University");
                case 7  -> topBySchool();
                case 8  -> topByYear();
                case 9  -> {
                    coordinator.announceTopResearcher();
                    Database.getInstance().save();
                    ConsoleUtils.success("Announcement created.");
                    ConsoleUtils.pressEnter();
                }
                case 10 -> updateCitations();
                case 11 -> addJournal();
                case 12 -> manageSupervisors();
                case 13 -> addPaperToProject();
                case 14 -> sendMessage();
                case 15 -> sendTechRequest();
                case 0  -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void makeResearcher() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.make.user.a.researcher")); ConsoleUtils.printLine();
        List<User> nonResearchers = Database.getInstance().getUsers().stream()
                .filter(u -> Database.getInstance().getResearcherFor(u) == null)
                .collect(Collectors.toList());
        if (nonResearchers.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.all.users.are.already.researchers"));
            ConsoleUtils.pressEnter(); return;
        }
        for (int i = 0; i < nonResearchers.size(); i++)
            System.out.printf("  %d. %-25s (%s)%n", i + 1,
                    nonResearchers.get(i).getName(),
                    nonResearchers.get(i).getClass().getSimpleName());
        System.out.println(LanguageManager.get("common.cancel"));
        int idx = ConsoleUtils.readInt("Select user: ");
        if (idx == 0 || idx > nonResearchers.size()) return;
        User target = nonResearchers.get(idx - 1);
        try {
            coordinator.makeResearcher(target);
            Database.getInstance().save();
            ConsoleUtils.success(target.getName() + " is now a Researcher.");
            if (target instanceof GraduateStudent gs) {
                ConsoleUtils.printLine();
                System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.this.is.a.graduate.student.assign.supervisor.now"));
                if (ConsoleUtils.readLine("(yes / Enter to skip): ").equalsIgnoreCase("yes"))
                    assignOrChangeSupervisor(gs);
            }
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void viewResearchers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.all.researchers")); ConsoleUtils.printLine();
        List<ResearcherDecorator<?>> researchers = coordinator.getAllResearchers();
        if (researchers.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); }
        else researchers.forEach(r -> System.out.printf(
                "  %-25s | h-index: %-3d | papers: %-3d | projects: %d%n",
                r.getName(), r.calculateHIndex(), r.getPapers().size(), r.getProjects().size()));
        ConsoleUtils.pressEnter();
    }

    private void createProject() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.create.research.project")); ConsoleUtils.printLine();
        String topic = ConsoleUtils.readLine("Topic (0=cancel): ");
        if (topic.equals("0")) return;
        ResearchProject project = coordinator.createProject(topic);
        Database.getInstance().save();
        ConsoleUtils.success("Project created: " + project.getTopic());
        ConsoleUtils.pressEnter();
    }

    private void viewProjects() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.all.research.projects")); ConsoleUtils.printLine();
        List<ResearchProject> projects = coordinator.getAllProjects();
        if (projects.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < projects.size(); i++)
            System.out.printf("  %d. %-35s | participants: %d | papers: %d%n",
                    i + 1, projects.get(i).getTopic(),
                    projects.get(i).getParticipants().size(),
                    projects.get(i).getPublishedPapers().size());
        int idx = ConsoleUtils.readInt("Select project to view details (0=back): ");
        if (idx < 1 || idx > projects.size()) return;
        viewProjectDetails(projects.get(idx - 1));
    }

    private void viewProjectDetails(ResearchProject project) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("coordinator.project.topic", project.getTopic())); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("coordinator.project.participants", project.getParticipants().size()));
        if (project.getParticipants().isEmpty()) System.out.println(LanguageManager.get("common.none"));
        else project.getParticipants().forEach(r ->
                System.out.printf("    %-30s h-index: %d%n", r.getName(), r.calculateHIndex()));
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("coordinator.project.papers", project.getPublishedPapers().size()));
        if (project.getPublishedPapers().isEmpty()) System.out.println(LanguageManager.get("common.none"));
        else project.getPublishedPapers().forEach(p -> System.out.println("    " + p));
        ConsoleUtils.pressEnter();
    }

    private void printPapers() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.print.all.papers")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.sort.1.by.date.2.by.citations.desc.3.by.pages"));
        Comparator<ResearchPaper> comp = switch (ConsoleUtils.readInt("Choice: ")) {
            case 1 -> Comparator.comparing(ResearchPaper::getPublishedDate);
            case 2 -> Comparator.comparingInt(ResearchPaper::getCitations).reversed();
            default -> Comparator.comparingInt(ResearchPaper::getPages);
        };
        coordinator.printAllPapers(comp);
        ConsoleUtils.pressEnter();
    }

    private void showTop(ResearcherDecorator<?> top, String label) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("coordinator.topCited.title", label)); ConsoleUtils.printLine();
        if (top == null) System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.researchers.found"));
        else {
            System.out.printf("  %-25s h-index: %d%n", top.getName(), top.calculateHIndex());
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.papers"));
            top.getPapers().stream()
                    .sorted(Comparator.comparingInt(ResearchPaper::getCitations).reversed())
                    .forEach(p -> System.out.printf("    %-40s citations: %d%n",
                            p.getTitle(), p.getCitations()));
        }
        ConsoleUtils.pressEnter();
    }

    private void topBySchool() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.top.cited.by.school")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.1.site.2.bs.3.ise.4.kma.5.oag.6.sge.7.sam.8.sche.0"));
        School school = switch (ConsoleUtils.readInt("School: ")) {
            case 1 -> School.SITE; case 2 -> School.BS; case 3 -> School.ISE;
            case 4 -> School.KMA; case 5 -> School.OAG; case 6 -> School.SGE;
            case 7 -> School.SAM; default -> School.SCHE;
        };
        showTop(coordinator.getTopCitedBySchool(school), school.toString());
    }

    private void topByYear() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.top.cited.by.year")); ConsoleUtils.printLine();
        int year = ConsoleUtils.readInt("Year (e.g. 2024): ");
        showTop(coordinator.getTopCitedByYear(year), String.valueOf(year));
    }

    private void updateCitations() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.update.paper.citations")); ConsoleUtils.printLine();
        List<ResearcherDecorator<?>> researchers = coordinator.getAllResearchers();
        if (researchers.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.researchers")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < researchers.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, researchers.get(i).getName());
        int rIdx = ConsoleUtils.readInt("Select researcher (0=cancel): ");
        if (rIdx == 0 || rIdx > researchers.size()) return;
        ResearcherDecorator<?> r = researchers.get(rIdx - 1);
        List<ResearchPaper> papers = r.getPapers();
        if (papers.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.papers")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < papers.size(); i++)
            System.out.printf("  %d. %-40s [citations: %d]%n",
                    i + 1, papers.get(i).getTitle(), papers.get(i).getCitations());
        int pIdx = ConsoleUtils.readInt("Select paper (0=cancel): ");
        if (pIdx == 0 || pIdx > papers.size()) return;
        int newCitations = ConsoleUtils.readInt("New citation count: ");
        try {
            papers.get(pIdx - 1).setCitations(newCitations);
            Database.getInstance().save();
            ConsoleUtils.success("Citations updated.");
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addJournal() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.add.journal.to.system")); ConsoleUtils.printLine();
        String name = ConsoleUtils.readLine("Journal name (0=cancel): ");
        if (name.equals("0")) return;
        Journal journal = new Journal(name, "General");
        Database.getInstance().addJournal(journal);
        Database.getInstance().save();
        ConsoleUtils.success("Journal '" + name + "' added. Users can now subscribe.");
        ConsoleUtils.pressEnter();
    }

    public void manageSupervisors() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.manage.supervisors")); ConsoleUtils.printLine();
        List<GraduateStudent> graduates = Database.getInstance().getAllStudents().stream()
                .filter(s -> s instanceof GraduateStudent)
                .map(s -> (GraduateStudent) s)
                .collect(Collectors.toList());
        if (graduates.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.graduate.students")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < graduates.size(); i++) {
            GraduateStudent gs = graduates.get(i);
            System.out.printf("  %d. %-25s [%s] supervisor: %s%n",
                    i + 1, gs.getName(), gs.getDegree(),
                    gs.getSupervisor() != null ? gs.getSupervisor().getName() : "NOT ASSIGNED");
        }
        int idx = ConsoleUtils.readInt("Select student (0=cancel): ");
        if (idx == 0 || idx > graduates.size()) return;
        assignOrChangeSupervisor(graduates.get(idx - 1));
    }

    public void assignOrChangeSupervisor(GraduateStudent gs) {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("coordinator.supervisor.for", gs.getName())); ConsoleUtils.printLine();
        List<ResearcherDecorator<?>> eligible = coordinator.getAllResearchers().stream()
                .filter(r -> r.calculateHIndex() >= 3 && !r.getInnerUser().equals(gs))
                .collect(Collectors.toList());
        if (eligible.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.eligible.supervisors.h.index.3"));
            ConsoleUtils.pressEnter(); return;
        }
        for (int i = 0; i < eligible.size(); i++)
            System.out.printf("  %d. %-25s h-index: %d%n",
                    i + 1, eligible.get(i).getName(), eligible.get(i).calculateHIndex());
        System.out.println(LanguageManager.get("common.cancel"));
        int idx = ConsoleUtils.readInt("Select supervisor: ");
        if (idx == 0 || idx > eligible.size()) return;
        try {
            if (gs.getSupervisor() == null) gs.setSupervisor(eligible.get(idx - 1));
            else gs.changeSupervisor(eligible.get(idx - 1));
            Database.getInstance().save();
            ConsoleUtils.success("Supervisor assigned: " + eligible.get(idx - 1).getName());
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void addPaperToProject() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.add.paper.to.project")); ConsoleUtils.printLine();
        List<ResearchProject> projects = coordinator.getAllProjects();
        if (projects.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.projects")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < projects.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, projects.get(i).getTopic());
        int pIdx = ConsoleUtils.readInt("Select project (0=cancel): ");
        if (pIdx == 0 || pIdx > projects.size()) return;
        ResearchProject project = projects.get(pIdx - 1);

        List<ResearchPaper> allPapers = coordinator.getAllResearchers().stream()
                .flatMap(r -> r.getPapers().stream())
                .distinct()
                .filter(p -> !project.getPublishedPapers().contains(p))
                .collect(Collectors.toList());
        if (allPapers.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearchCoordinatorMenu.no.papers.to.add")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < allPapers.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, allPapers.get(i).getTitle());
        int paperIdx = ConsoleUtils.readInt("Select paper (0=cancel): ");
        if (paperIdx == 0 || paperIdx > allPapers.size()) return;
        project.addPaper(allPapers.get(paperIdx - 1));
        Database.getInstance().save();
        ConsoleUtils.success("Paper added to project.");
        ConsoleUtils.pressEnter();
    }

    private void sendMessage() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendMessage.title")); ConsoleUtils.printLine();
        List<Employee> employees = Database.getInstance().getUsers().stream()
                .filter(u -> u instanceof Employee && !u.equals(coordinator))
                .map(u -> (Employee) u)
                .collect(Collectors.toList());
        for (int i = 0; i < employees.size(); i++)
            System.out.printf("  %d. %s (%s)%n", i + 1,
                    employees.get(i).getName(), employees.get(i).getClass().getSimpleName());
        int idx = ConsoleUtils.readInt("Select recipient (0=cancel): ");
        if (idx < 1 || idx > employees.size()) return;
        String text = ConsoleUtils.readLine("Message: ");
        coordinator.sendMessage(employees.get(idx - 1), text);
        Database.getInstance().save();
        ConsoleUtils.success("Message sent.");
        ConsoleUtils.pressEnter();
    }

    private void sendTechRequest() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("common.sendTechRequest.title")); ConsoleUtils.printLine();
        String desc = ConsoleUtils.readLine("Description (0=cancel): ");
        if (desc.equals("0")) return;
        coordinator.sendTechRequest(desc);
        Database.getInstance().save();
        ConsoleUtils.success("Tech request submitted.");
        ConsoleUtils.pressEnter();
    }
}
