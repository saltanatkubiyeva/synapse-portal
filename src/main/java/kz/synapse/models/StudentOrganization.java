package kz.synapse.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentOrganization implements Serializable {
    private String name;
    private List<Student> members = new ArrayList<>();
    private Student head;

    public StudentOrganization(String name) {
        this.name = name;
    }

    public void addMember(Student student) {
        if (!members.contains(student)) {
            members.add(student);
        }
    }

    public void removeMember(Student student) {
        members.remove(student);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Student> getMembers() { return members; }

    public Student getHead() { return head; }
    public void setHead(Student head) {
        if (!members.contains(head))
            throw new IllegalArgumentException(
                    head.getName() + " is not a member of this organization"
            );
        this.head = head;
    }
}
