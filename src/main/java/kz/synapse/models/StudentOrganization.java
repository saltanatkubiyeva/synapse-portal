package kz.synapse.models;

import java.util.List;

public class StudentOrganization {
    private String name;
    private List<Student> members;
    private Student leader;

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

    public void setLeader(Student student) {
        this.leader = student;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getMembers() {
        return members;
    }

    public void setMembers(List<Student> members) {
        this.members = members;
    }

    public Student getLeader() {
        return leader;
    }
}
