package kz.synapse.models;

import kz.synapse.enums.RequestStatus;
import java.time.LocalDateTime;

public class Request {
    private String description;
    private RequestStatus status;
    private String signedBy;
    private Employee requester;
    private LocalDateTime createdAt;

    public Request(String description, Employee requester) {
        this.description = description;
        this.requester = requester;
        this.status = RequestStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public Employee getRequester() {
        return requester;
    }

    public void setRequester(Employee requester) {
        this.requester = requester;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
