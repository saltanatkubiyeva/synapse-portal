package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.NewsType;
import kz.synapse.interfaces.Researcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResearcherDecorator implements Researcher, Serializable {

    private User baseUser;
    private List<ResearchPaper> papers = new ArrayList<>();
    private List<ResearchProject> projects = new ArrayList<>();

    public ResearcherDecorator(User baseUser) {
        this.baseUser = baseUser;
    }

    // имя

    @Override
    public String getName() {
        return baseUser.getName();
    }

    // статьи

    @Override
    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);

        // announcement для всех
        Database.getInstance().addNews(new News(
                "Researcher " + getName()
                        + " published: " + paper.getTitle(),
                NewsType.RESEARCH
        ));

        logAction("Published paper: " + paper.getTitle());
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return papers;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        System.out.println("--- Papers of " + getName() + " ---");
        papers.stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }

    // h-index

    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;

        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort((p1, p2) ->
                Integer.compare(p2.getCitations(), p1.getCitations())
        );

        int hIndex = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getCitations() >= i + 1)
                hIndex = i + 1;
            else
                break;
        }
        return hIndex;
    }

    // проекты

    @Override
    public void joinProject(ResearchProject project) {
        project.addParticipant(this);
        projects.add(project);
        logAction("Joined project: " + project.getTopic());
    }

    @Override
    public List<ResearchProject> getProjects() {
        return projects;
    }

    // геттер baseUser
    public User getBaseUser() { return baseUser; }

    // логирование
    private void logAction(String action) {
        Database.getInstance().addUserLog(
                "[" + java.time.LocalDateTime.now() + "] "
                        + baseUser.getClass().getSimpleName()
                        + " " + getName() + ": " + action
        );
    }

    @Override
    public String toString() {
        return String.format(
                "Researcher{name='%s', papers=%d, hIndex=%d, projects=%d}",
                getName(), papers.size(), calculateHIndex(), projects.size()
        );
    }
}