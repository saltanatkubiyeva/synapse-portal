package kz.synapse.models;

import kz.synapse.exceptions.NotResearcherException;
import kz.synapse.interfaces.Researcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {

    private String topic;
    private List<Researcher> participants = new ArrayList<>();
    private List<ResearchPaper> publishedPapers = new ArrayList<>();

    public ResearchProject(String topic) {
        this.topic = topic;
    }

    // только Researcher может вступить
    public void addParticipant(Researcher researcher) {
        if (!participants.contains(researcher))
            participants.add(researcher);
    }

    // если не Researcher — exception
    public void addParticipant(User user) {
        throw new NotResearcherException();
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }

    public String getTopic()                        { return topic; }
    public List<Researcher> getParticipants()       { return participants; }
    public List<ResearchPaper> getPublishedPapers() { return publishedPapers; }

    @Override
    public String toString() {
        return String.format(
                "ResearchProject{topic='%s', participants=%d, papers=%d}",
                topic, participants.size(), publishedPapers.size()
        );
    }
}