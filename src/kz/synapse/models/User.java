package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.interfaces.JournalObserver;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** базовый юзер */
public abstract class User implements JournalObserver, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private String email;
    private String password;
    private Language language;
    private boolean isBanned = false;

    // уведомления
    private List<Notification> notifications = new ArrayList<>();

    public User(String id, String name, String email,
                String password, Language language) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.language = language;
    }


    /** журнал опубликовал статью */
    @Override
    public void update(Journal journal, ResearchPaper paper) {
        String text = "📰 New paper in «" + journal.getName()
                + "»: " + paper.getTitle()
                + " by " + paper.getAuthors();
        notifications.add(new Notification(text));
    }

    // подписка на журналы

    public void subscribeToJournal(Journal j) {
        j.subscribe(this);
        logAction("Subscribed to journal: " + j.getName());
    }

    public void unsubscribeFromJournal(Journal j) {
        j.unsubscribe(this);
        logAction("Unsubscribed from journal: " + j.getName());
    }

    /** заявка в техподдержку */
    public Request sendTechRequest(String description) {
        Request request = new Request(description, this);
        kz.synapse.database.Database.getInstance().addTechRequest(request);
        logAction("Sent tech request: " + description);
        return request;
    }

    // уведомления

    public List<Notification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    /** непрочитанные */
    public List<Notification> getUnreadNotifications() {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    /** прочитать все */
    public void markAllNotificationsRead() {
        notifications.forEach(Notification::markRead);
    }

    // прочее

    public void switchLanguage(Language newLanguage) {
        if (newLanguage != null) this.language = newLanguage;
    }

    protected void logAction(String action) {
        String log = String.format("[%s] %s %s: %s",
                LocalDateTime.now(),
                getClass().getSimpleName(),
                getName(),
                action);
        Database.getInstance().addUserLog(log);
    }

    // геттеры / сеттеры

    public String getId()                        { return id; }
    public String getName()                      { return name; }
    public String getEmail()                     { return email; }
    public String getPassword()                  { return password; }
    public Language getLanguage()                { return language; }
    public boolean isBanned()                    { return isBanned; }
    public void setBanned(boolean banned)        { this.isBanned = banned; }
    public void setName(String name)             { this.name = name; }
    public void setEmail(String email)           { this.email = email; }
    public void setPassword(String password)     { this.password = password; }

    // equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', language=%s}",
                id, name, email, language);
    }
}
