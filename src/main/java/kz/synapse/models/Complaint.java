package kz.synapse.models;

import kz.synapse.enums.UrgencyLevel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Complaint implements Serializable {

    private Teacher sender;
    private List<Student> aboutStudents;
    private UrgencyLevel urgency;
    private String reason;
    private LocalDateTime createdAt;

    public Complaint(Teacher sender, List<Student> aboutStudents,
                     UrgencyLevel urgency, String reason) {
        this.sender = sender;
        this.aboutStudents = aboutStudents;
        this.urgency = urgency;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    // геттеры
    public Teacher getSender()                { return sender; }
    public List<Student> getAboutStudents()   { return aboutStudents; }
    public UrgencyLevel getUrgency()          { return urgency; }
    public String getReason()                 { return reason; }
    public LocalDateTime getCreatedAt()       { return createdAt; }

    @Override
    public String toString() {
        return String.format(
                "Complaint{from='%s', students=%d, urgency=%s, reason='%s'}",
                sender.getName(), aboutStudents.size(), urgency, reason
        );
    }
}