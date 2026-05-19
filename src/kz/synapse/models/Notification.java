package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String text;
    private boolean isRead;
    private final LocalDateTime createdAt;

    public Notification(String text) {
        this.text      = text;
        this.isRead    = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markRead()              { this.isRead = true; }

    public String getText()             { return text; }
    public boolean isRead()             { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification n = (Notification) o;
        return Objects.equals(text, n.text)
                && Objects.equals(createdAt, n.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, createdAt);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s",
                createdAt, isRead ? "(read)" : "(unread)", text);
    }
}
