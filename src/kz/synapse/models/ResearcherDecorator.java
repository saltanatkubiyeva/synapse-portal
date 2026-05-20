package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.NewsType;
import kz.synapse.interfaces.Researcher;
import kz.synapse.utils.LanguageManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResearcherDecorator<T extends User>
        implements Researcher, Serializable {

    private static final long serialVersionUID = 1L;

    private final T innerUser;
    private final List<ResearchPaper>   papers   = new ArrayList<>();
    private final List<ResearchProject> projects = new ArrayList<>();

    public ResearcherDecorator(T innerUser) {
        this.innerUser = innerUser;
    }

    // вернуть внутреннего юзера
    public T getInnerUser() {
        return innerUser;
    }

    @Override
    public String getName() {
        return innerUser.getName();
    }

    // опубликовать статью
    @Override
    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);

        News news = new News(
                "Researcher " + getName() + " published: " + paper.getTitle(),
                NewsType.RESEARCH
        );
        Database.getInstance().addNews(news);

        logAction("Published paper: " + paper.getTitle());
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return Collections.unmodifiableList(papers);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        System.out.println(LanguageManager.get("research.papersOf", getName()));
        papers.stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }

    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;

        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort((a, b) -> Integer.compare(b.getCitations(), a.getCitations()));

        int h = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getCitations() >= i + 1)
                h = i + 1;
            else
                break;
        }
        return h;
    }

    @Override
    public void joinProject(ResearchProject project) {
        project.addParticipant(this);
        projects.add(project);
        logAction("Joined project: " + project.getTopic());
    }

    @Override
    public List<ResearchProject> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    private void logAction(String action) {
        String entry = String.format("[%s] %s %s: %s",
                java.time.LocalDateTime.now(),
                innerUser.getClass().getSimpleName(),
                getName(),
                action);
        Database.getInstance().addUserLog(entry);
    }

    @Override
    public String toString() {
        return String.format(
                "Researcher{name='%s', type=%s, papers=%d, hIndex=%d, projects=%d}",
                getName(),
                innerUser.getClass().getSimpleName(),
                papers.size(),
                calculateHIndex(),
                projects.size()
        );
    }
}
