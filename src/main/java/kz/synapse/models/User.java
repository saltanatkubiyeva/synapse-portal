package kz.synapse.models;

import kz.synapse.enums.Language;

import javax.management.NotificationBroadcaster;
import java.io.Serializable;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import kz.synapse.interfaces.JournalObserver;
import kz.synapse.database.Database;

public abstract class User implements JournalObserver, Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private Language language;
    private boolean isBanned = false;
    private List<Notification> notifications = new ArrayList<>();

    public User(String id, String name, String email, String password, Language language) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.language = language;
    }

    // JournalObserver
    @Override
    public void update(Journal journal, ResearchPaper paper) {
        String text = "New paper in " + journal.getName()
                + ": " + paper.getTitle();
        this.notifications.add(new Notification(text));
    }

    // subscribing/unsubscribing

    public void subscribeToJournal(Journal j) {
        j.addObserver(this);
    }

    public void unsubscribeFromJournal(Journal j) {
        j.removeObserver(this);
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Language getLanguage() { return language; }
    public List<Notification> getNotifications() { return notifications; }
    public boolean isBanned() { return isBanned; }

    public void setBanned(boolean banned) { this.isBanned = banned; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public void switchLanguage(Language newLanguage) {
        if (newLanguage != null) {
            this.language = newLanguage;
        }
    }

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
    // User.java — добавить метод
    protected void logAction(String action) {
        String log = String.format("[%s] %s %s: %s",
                LocalDateTime.now(),
                getClass().getSimpleName(),
                getName(),
                action);
        Database.getInstance().addUserLog(log);
    }
}
