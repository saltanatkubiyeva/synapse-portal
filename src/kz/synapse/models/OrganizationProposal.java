package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrganizationProposal implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status { PENDING, APPROVED, REJECTED }

    private final String name;
    private final String description;
    private final Student proposer;
    private Status status;
    private final LocalDateTime createdAt;

    public OrganizationProposal(String name, String description, Student proposer) {
        this.name        = name;
        this.description = description;
        this.proposer    = proposer;
        this.status      = Status.PENDING;
        this.createdAt   = LocalDateTime.now();
    }

    public void approve() { this.status = Status.APPROVED; }
    public void reject()  { this.status = Status.REJECTED; }

    public String getName()            { return name; }
    public String getDescription()     { return description; }
    public Student getProposer()       { return proposer; }
    public Status getStatus()          { return status; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizationProposal)) return false;
        OrganizationProposal p = (OrganizationProposal) o;
        return Objects.equals(name, p.name) && Objects.equals(createdAt, p.createdAt);
    }

    @Override
    public int hashCode() { return Objects.hash(name, createdAt); }

    @Override
    public String toString() {
        return String.format("OrganizationProposal{name='%s', proposer='%s', status=%s}",
                name, proposer.getName(), status);
    }
}
