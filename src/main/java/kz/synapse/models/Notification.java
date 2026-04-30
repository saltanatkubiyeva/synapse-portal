package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {

    private String text;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification(String text) {
        this.text = text;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    // прочитать
    public void markRead() { this.isRead = true; }

    // геттеры
    public String getText()             { return text; }
    public boolean isRead()             { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s",
                createdAt,
                isRead ? "(read)" : "(unread)",
                text);
    }
}