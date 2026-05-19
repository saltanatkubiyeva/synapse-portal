package kz.synapse.models;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.enums.NewsType;
import kz.synapse.enums.School;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResearchCoordinator extends Employee {

    private static final long serialVersionUID = 1L;

    public ResearchCoordinator(String id, String name, String email,
                               String password, Language language) {
        super(id, name, email, password, language);
    }

    // researchers

    /** динамически назначает пользователю роль исследователя */
    public <T extends User> ResearcherDecorator<T> makeResearcher(T user) {
        ResearcherDecorator<T> decorator =
                kz.synapse.factory.UserFactory.wrapAsResearcher(user);
        logAction("Made researcher: " + user.getName());
        return decorator;
    }

    public List<ResearcherDecorator<?>> getAllResearchers() {
        return Database.getInstance().getResearchers();
    }

    // projects

    public ResearchProject createProject(String topic) {
        ResearchProject project = new ResearchProject(topic);
        Database.getInstance().addProject(project);
        logAction("Created project: " + topic);
        return project;
    }

    public List<ResearchProject> getAllProjects() {
        return Database.getInstance().getProjects();
    }

    // papers

    public void printAllPapers(Comparator<ResearchPaper> comparator) {
        System.out.println(LanguageManager.get("models.ResearchCoordinator.all.research.papers"));
        Database.getInstance().getResearchers().stream()
                .flatMap(r -> r.getPapers().stream())
                .sorted(comparator)
                .forEach(System.out::println);
    }

    public List<ResearchPaper> getPapersByYear(int year) {
        return Database.getInstance().getResearchers().stream()
                .flatMap(r -> r.getPapers().stream())
                .filter(p -> p.getPublishedDate().getYear() == year)
                .collect(Collectors.toList());
    }

    // top-cited researchers

    public ResearcherDecorator<?> getTopCitedResearcher() {
        return Database.getInstance().getResearchers().stream()
                .max(Comparator.comparingInt(this::totalCitations))
                .orElse(null);
    }

    public ResearcherDecorator<?> getTopCitedBySchool(School school) {
        return Database.getInstance().getResearchers().stream()
                .filter(r -> schoolOf(r) == school)
                .max(Comparator.comparingInt(this::totalCitations))
                .orElse(null);
    }

    public ResearcherDecorator<?> getTopCitedByYear(int year) {
        return Database.getInstance().getResearchers().stream()
                .max(Comparator.comparingInt(r ->
                        r.getPapers().stream()
                                .filter(p -> p.getPublishedDate().getYear() == year)
                                .mapToInt(ResearchPaper::getCitations)
                                .sum()))
                .orElse(null);
    }

    // announcement

    public void announceTopResearcher() {
        ResearcherDecorator<?> top = getTopCitedResearcher();
        if (top == null) return;

        int citations = totalCitations(top);
        News news = new News(
                "Top cited researcher: " + top.getName()
                        + " | h-index: " + top.calculateHIndex()
                        + " | total citations: " + citations,
                NewsType.RESEARCH
        );
        Database.getInstance().addNews(news);
        logAction("Announced top researcher: " + top.getName());
    }

    private int totalCitations(ResearcherDecorator<?> r) {
        return r.getPapers().stream()
                .mapToInt(ResearchPaper::getCitations)
                .sum();
    }

    private School schoolOf(ResearcherDecorator<?> r) {
        User inner = r.getInnerUser();
        if (inner instanceof Teacher)   return ((Teacher) inner).getSchool();
        if (inner instanceof Student)   return ((Student) inner).getSchool();
        return null;
    }

    @Override
    public String toString() {
        return String.format("ResearchCoordinator{name='%s', email='%s'}",
                getName(), getEmail());
    }
}
