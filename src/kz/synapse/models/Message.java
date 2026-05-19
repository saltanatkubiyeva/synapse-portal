package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/** прямое сообщение между сотрудниками (direct message). */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Employee sender;
    private final Employee receiver;
    private final String text;
    private final LocalDateTime sentAt;
    private boolean isRead;

    public Message(Employee sender, Employee receiver, String text) {
        this.sender   = sender;
        this.receiver = receiver;
        this.text     = text;
        this.sentAt   = LocalDateTime.now();
        this.isRead   = false;
    }

    public void markRead() { this.isRead = true; }

    public Employee getSender()         { return sender; }
    public Employee getReceiver()       { return receiver; }
    public String getText()             { return text; }
    public LocalDateTime getSentAt()    { return sentAt; }
    public boolean isRead()             { return isRead; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message m = (Message) o;
        return Objects.equals(sender, m.sender)
                && Objects.equals(receiver, m.receiver)
                && Objects.equals(sentAt, m.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, sentAt);
    }

    @Override
    public String toString() {
        return String.format("[%s] From %s → %s: %s%s",
                sentAt, sender.getName(), receiver.getName(),
                text, isRead ? "" : " (unread)");
    }
}
