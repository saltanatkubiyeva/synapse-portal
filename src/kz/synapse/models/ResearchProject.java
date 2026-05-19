package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.exceptions.NotResearcherException;
import kz.synapse.interfaces.Researcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String topic;
    private final List<Researcher> participants   = new ArrayList<>();
    private final List<ResearchPaper> papers      = new ArrayList<>();

    public ResearchProject(String topic) {
        this.topic = topic;
    }

    // вступление в проект

    public void tryJoin(Object candidate) {
        if (!(candidate instanceof Researcher))
            throw new NotResearcherException(
                    candidate instanceof User
                            ? ((User) candidate).getName()
                            : candidate.toString());
        addParticipant((Researcher) candidate);
    }

    /**добавляет подтверждённого researcher. */
    public void addParticipant(Researcher researcher) {
        if (!participants.contains(researcher))
            participants.add(researcher);
    }

    public void addPaper(ResearchPaper paper) { papers.add(paper); }

    // геттеры

    public String getTopic()                        { return topic; }
    public List<Researcher> getParticipants()       { return participants; }
    public List<ResearchPaper> getPublishedPapers() { return papers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        ResearchProject p = (ResearchProject) o;
        return Objects.equals(topic, p.topic);
    }

    @Override
    public int hashCode() { return Objects.hash(topic); }

    @Override
    public String toString() {
        return String.format("ResearchProject{topic='%s', participants=%d, papers=%d}",
                topic, participants.size(), papers.size());
    }
}
