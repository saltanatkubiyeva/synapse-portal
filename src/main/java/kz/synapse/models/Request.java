package kz.synapse.models;

import kz.synapse.enums.RequestStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Request implements Serializable {

    private String description;
    private RequestStatus status;
    private Employee requester;
    private LocalDateTime createdAt;

    public Request(String description, Employee requester) {
        this.description = description;
        this.requester = requester;
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    // геттеры
    public String getDescription()      { return description; }
    public RequestStatus getStatus()    { return status; }
    public Employee getRequester()      { return requester; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // сеттер
    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format(
                "Request{description='%s', status=%s, from='%s'}",
                description, status, requester.getName()
        );
    }
}