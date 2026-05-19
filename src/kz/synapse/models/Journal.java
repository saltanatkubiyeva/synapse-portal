package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.NewsType;
import kz.synapse.interfaces.JournalObserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Journal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String topic;

    private transient List<JournalObserver> subscribers = new ArrayList<>();
    private List<String> subscriberIds = new ArrayList<>();

    private final List<ResearchPaper> papers = new ArrayList<>();

    public Journal(String name, String topic) {
        this.name  = name;
        this.topic = topic;
    }

    // observer — подписка

    public void subscribe(JournalObserver observer) {
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
            if (observer instanceof User)
                subscriberIds.add(((User) observer).getId());
        }
    }

    public void unsubscribe(JournalObserver observer) {
        subscribers.remove(observer);
        if (observer instanceof User)
            subscriberIds.remove(((User) observer).getId());
    }

    public void publish(ResearchPaper paper) {
        papers.add(paper);
        notifySubscribers(paper);
        Database.getInstance().addNews(new News(
                "New paper in «" + name + "»: " + paper.getTitle()
                        + " by " + paper.getAuthors(),
                NewsType.RESEARCH));
    }

    private void notifySubscribers(ResearchPaper paper) {
        for (JournalObserver obs : subscribers)
            obs.update(this, paper);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // восстанавливаем transient список из ID
        subscribers = new ArrayList<>();
        if (subscriberIds != null) {
            for (String id : subscriberIds) {
                User u = Database.getInstance().findById(id);
                if (u != null) subscribers.add(u);
            }
        }
    }

    // геттеры

    public String getName()                       { return name; }
    public String getTopic()                      { return topic; }
    public List<ResearchPaper> getPapers()        { return papers; }
    public List<JournalObserver> getSubscribers() { return subscribers; }
    public List<String> getSubscriberIds()        { return subscriberIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        return Objects.equals(name, ((Journal) o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() {
        return String.format("Journal{name='%s', topic='%s', subscribers=%d, papers=%d}",
                name, topic, subscribers.size(), papers.size());
    }
}
