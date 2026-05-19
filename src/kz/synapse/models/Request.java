package kz.synapse.models;

import kz.synapse.enums.RequestStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String description;
    private RequestStatus status;
    private final String requesterName; // имя для отображения
    private final LocalDateTime createdAt;

    public Request(String description, User requester) {
        this.description   = description;
        this.requesterName = requester != null ? requester.getName() : "Unknown";
        this.status        = RequestStatus.NEW;
        this.createdAt     = LocalDateTime.now();
    }

    public void setStatus(RequestStatus status) { this.status = status; }

    public String getDescription()          { return description; }
    public RequestStatus getStatus()        { return status; }
    public String getRequesterName()        { return requesterName; }
    public LocalDateTime getCreatedAt()     { return createdAt; }

    public User getRequester()              { return null; } 

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request r = (Request) o;
        return Objects.equals(requesterName, r.requesterName)
                && Objects.equals(createdAt, r.createdAt);
    }

    @Override
    public int hashCode() { return Objects.hash(requesterName, createdAt); }

    @Override
    public String toString() {
        return String.format("Request{description='%s', status=%s, from='%s', at=%s}",
                description, status, requesterName, createdAt.toLocalDate());
    }
}
