package kz.synapse.ui.menus;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.CitationFormat;
import kz.synapse.models.*;
import kz.synapse.ui.ConsoleUtils;
import kz.synapse.ui.UIStrings;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ResearcherMenu {

    private final ResearcherDecorator<?> researcher;

    public ResearcherMenu(ResearcherDecorator<?> researcher) {
        this.researcher = researcher;
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("menu.researcher");
            System.out.println("  " + researcher.getName()
                    + "  |  h-index: " + researcher.calculateHIndex()
                    + "  |  Papers: " + researcher.getPapers().size()
                    + "  |  Projects: " + researcher.getProjects().size());
            ConsoleUtils.printLine();
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.1.publish.paper"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.2.view.my.papers"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.3.sort.papers"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.4.get.citation.plain.bibtex"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.5.join.research.project"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.6.view.my.projects"));
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.7.calculate.h.index"));
            System.out.println("  " + UIStrings.get("msg.back"));
            ConsoleUtils.printLine();

            switch (ConsoleUtils.readInt(UIStrings.get("prompt.choice"))) {
                case 1 -> publishPaper();
                case 2 -> viewPapers();
                case 3 -> sortPapers();
                case 4 -> getCitation();
                case 5 -> joinProject();
                case 6 -> viewProjects();
                case 7 -> {
                    System.out.println(LanguageManager.get("researcher.hindex", researcher.calculateHIndex()));
                    ConsoleUtils.pressEnter();
                }
                case 0 -> { return; }
                default -> System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    private void publishPaper() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.publish.paper")); ConsoleUtils.printLine();
        String title   = ConsoleUtils.readLine("Title: ");
        String authors = ConsoleUtils.readLine("Authors (comma-separated): ");
        String journal = ConsoleUtils.readLine("Journal: ");
        int pages      = ConsoleUtils.readInt("Pages: ");
        String doi     = ConsoleUtils.readLine("DOI: ");
        String dateStr = ConsoleUtils.readLine("Published date (YYYY-MM-DD, Enter=today): ");
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        ResearchPaper paper = new ResearchPaper(title, authors, journal, pages, date, doi);

        // спросить — публиковать в журнале?
        List<Journal> journals = Database.getInstance().getJournals();
        if (!journals.isEmpty()) {
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.publish.in.a.journal.for.subscriber.notifications"));
            for (int i = 0; i < journals.size(); i++)
                System.out.printf("  %d. %s%n", i + 1, journals.get(i).getName());
            System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.0.no.journal"));
            int jIdx = ConsoleUtils.readInt("Choice: ");
            if (jIdx >= 1 && jIdx <= journals.size()) {
                journals.get(jIdx - 1).publish(paper);
                ConsoleUtils.success("Paper published in journal + announcement created.");
            } else {
                researcher.publishPaper(paper);
                ConsoleUtils.success("Paper published. Announcement created.");
            }
        } else {
            researcher.publishPaper(paper);
            ConsoleUtils.success("Paper published. Announcement created.");
        }
        Database.getInstance().save();
        ConsoleUtils.pressEnter();
    }

    private void viewPapers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.my.papers")); ConsoleUtils.printLine();
        List<ResearchPaper> papers = researcher.getPapers();
        if (papers.isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else papers.forEach(p -> System.out.println("  " + p));
        ConsoleUtils.pressEnter();
    }

    private void sortPapers() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.sort.papers")); ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.1.by.date.published"));
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.2.by.citations.desc"));
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.3.by.pages"));
        Comparator<ResearchPaper> comp = switch (ConsoleUtils.readInt("Choice: ")) {
            case 1 -> Comparator.comparing(ResearchPaper::getPublishedDate);
            case 2 -> Comparator.comparingInt(ResearchPaper::getCitations).reversed();
            default -> Comparator.comparingInt(ResearchPaper::getPages);
        };
        researcher.printPapers(comp);
        ConsoleUtils.pressEnter();
    }

    private void getCitation() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.get.citation")); ConsoleUtils.printLine();
        List<ResearchPaper> papers = researcher.getPapers();
        if (papers.isEmpty()) { System.out.println(UIStrings.get("msg.empty")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < papers.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, papers.get(i).getTitle());
        int idx = ConsoleUtils.readInt("Select paper (0=cancel): ");
        if (idx < 1 || idx > papers.size()) return;
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.format.1.plain.text.2.bibtex"));
        CitationFormat fmt = ConsoleUtils.readInt("Choice: ") == 1
                ? CitationFormat.PLAIN_TEXT : CitationFormat.BIBTEX;
        System.out.println("\n" + papers.get(idx - 1).getCitation(fmt));
        ConsoleUtils.pressEnter();
    }

    private void joinProject() {
        ConsoleUtils.clearScreen();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.join.research.project")); ConsoleUtils.printLine();
        List<ResearchProject> projects = Database.getInstance().getProjects();
        if (projects.isEmpty()) { System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.no.projects.available")); ConsoleUtils.pressEnter(); return; }
        for (int i = 0; i < projects.size(); i++)
            System.out.printf("  %d. %s | %d participants%n",
                    i + 1, projects.get(i).getTopic(), projects.get(i).getParticipants().size());
        int idx = ConsoleUtils.readInt("Select project (0=cancel): ");
        if (idx < 1 || idx > projects.size()) return;
        try {
            researcher.joinProject(projects.get(idx - 1));
            Database.getInstance().save();
            ConsoleUtils.success("Joined project: " + projects.get(idx - 1).getTopic());
        } catch (Exception e) { ConsoleUtils.error(e.getMessage()); }
        ConsoleUtils.pressEnter();
    }

    private void viewProjects() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printLine();
        System.out.println(LanguageManager.get("ui.menus.ResearcherMenu.my.projects")); ConsoleUtils.printLine();
        if (researcher.getProjects().isEmpty()) System.out.println(UIStrings.get("msg.empty"));
        else researcher.getProjects().forEach(p -> System.out.println("  " + p));
        ConsoleUtils.pressEnter();
    }
}
