package kz.synapse.models;

import kz.synapse.interfaces.JournalObserver;
import kz.synapse.enums.NewsType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Journal implements Serializable {

    private String name;
    private String topic;
    private List<JournalObserver> subscribers; // observers
    private List<ResearchPaper> papers;

    public Journal(String name, String topic) {
        this.name = name;
        this.topic = topic;
        this.subscribers = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    public void addObserver(JournalObserver o) {
        if (!subscribers.contains(o))
            subscribers.add(o);
    }

    public void removeObserver(JournalObserver o) {
        subscribers.remove(o);
    }

    // publication
    public void publish(ResearchPaper paper) {
        papers.add(paper);

        // notification for subscribers only
        notifyObservers(paper);

        // ШАГ 2: публичная новость для всех
        News news = new News(
                "New research: " + paper.getTitle() +
                        " by " + paper.getAuthors(),
                NewsType.RESEARCH
        );

        Database.getInstance().addNews(news);
    }

    // notify method
    private void notifyObservers(ResearchPaper paper) {
        for (JournalObserver observer : subscribers) {
            observer.update(this, paper);
        }
    }

    public String getName()  { return name; }
    public String getTopic() { return topic; }
    public List<ResearchPaper> getPapers() { return papers; }
    public List<JournalObserver> getSubscribers() { return subscribers; }

    @Override
    public String toString() {
        return String.format("Journal{name='%s', topic='%s', subscribers=%d}",
                name, topic, subscribers.size());
    }
}