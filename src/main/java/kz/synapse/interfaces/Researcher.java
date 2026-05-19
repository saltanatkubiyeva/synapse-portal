package kz.synapse.interfaces;

import kz.synapse.models.ResearchPaper;
import kz.synapse.models.ResearchProject;
import java.util.Comparator;
import java.util.List;

public interface Researcher {
    String getName();

    // статьи
    void publishPaper(ResearchPaper paper);
    List<ResearchPaper> getPapers();
    void printPapers(Comparator<ResearchPaper> comparator);

    // метрики
    int calculateHIndex();

    // проекты
    void joinProject(ResearchProject project);
    List<ResearchProject> getProjects();
}