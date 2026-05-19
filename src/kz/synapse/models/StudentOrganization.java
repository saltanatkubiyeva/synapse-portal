package kz.synapse.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StudentOrganization implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private final List<Student> members       = new ArrayList<>();
    private final List<Student> joinRequests  = new ArrayList<>();
    private Student head;

    public StudentOrganization(String name, String description, Student founder) {
        this.name        = name;
        this.description = description;
        members.add(founder);
        this.head = founder;
    }

    public void requestJoin(Student student) {
        if (members.contains(student))
            throw new IllegalStateException(student.getName() + " is already a member.");
        if (joinRequests.contains(student))
            throw new IllegalStateException(student.getName() + " already has a pending request.");
        joinRequests.add(student);
    }

    public void approveJoin(Student student) {
        if (!joinRequests.contains(student))
            throw new IllegalArgumentException(student.getName() + " has no pending join request.");
        joinRequests.remove(student);
        members.add(student);
        student.addNotification(new Notification("Your request to join '" + name + "' was approved."));
    }

    public void rejectJoin(Student student) {
        if (!joinRequests.remove(student))
            throw new IllegalArgumentException(student.getName() + " has no pending join request.");
        student.addNotification(new Notification("Your request to join '" + name + "' was rejected."));
    }

    public void leave(Student student) {
        if (student.equals(head))
            throw new IllegalStateException(
                    "Head must transfer leadership before leaving. Use transferLeadership() first.");
        if (!members.remove(student))
            throw new IllegalArgumentException(student.getName() + " is not a member.");
    }

    public void transferLeadership(Student successor) {
        if (!members.contains(successor))
            throw new IllegalArgumentException(successor.getName() + " is not a member of this organization.");
        if (successor.equals(head))
            throw new IllegalArgumentException(successor.getName() + " is already the head.");
        this.head = successor;
    }

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }
    public String getDescription()      { return description; }
    public Student getHead()            { return head; }
    public List<Student> getMembers()   { return Collections.unmodifiableList(members); }
    public List<Student> getJoinRequests() { return Collections.unmodifiableList(joinRequests); }
    public boolean isMember(Student s)  { return members.contains(s); }
    public boolean hasPendingRequest(Student s) { return joinRequests.contains(s); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentOrganization)) return false;
        return Objects.equals(name, ((StudentOrganization) o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() {
        return String.format("StudentOrganization{name='%s', members=%d, head='%s'}",
                name, members.size(), head != null ? head.getName() : "none");
    }
}
