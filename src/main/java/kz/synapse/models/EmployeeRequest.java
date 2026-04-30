package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmployeeRequest implements Serializable {

    private Employee sender;
    private String topic;
    private String content;
    private boolean signedByDean;
    private String signedBy;
    private LocalDateTime createdAt;

    public EmployeeRequest(Employee sender, String topic, String content) {
        this.sender = sender;
        this.topic = topic;
        this.content = content;
        this.signedByDean = false;
        this.createdAt = LocalDateTime.now();
    }

    // dean подписывает
    public void signByDean(Dean dean) {
        this.signedByDean = true;
        this.signedBy = dean.getName();
    }

    // геттеры
    public Employee getSender()         { return sender; }
    public String getTopic()            { return topic; }
    public String getContent()          { return content; }
    public boolean isSignedByDean()     { return signedByDean; }
    public String getSignedBy()         { return signedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format(
                "EmployeeRequest{from='%s', topic='%s', signedByDean=%b, signedBy='%s'}",
                sender.getName(), topic, signedByDean,
                signedBy != null ? signedBy : "not signed"
        );
    }
}