package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.*;

import java.util.List;

public class ORManager extends Manager {

    private static final long serialVersionUID = 1L;

    public ORManager(String id, String name, String email,
                     String password, Language language) {
        super(id, name, email, password, language, ManagerType.OR);
    }

    public void openSemester(SemesterType semester) {
        Database.getInstance().setCurrentSemester(semester);
        Database.getInstance().setRegistrationOpen(true);
        addNews("Registration for " + semester + " is OPEN",
                "Max 21 credits per semester.", NewsType.ANNOUNCEMENT);
        logAction("Opened semester: " + semester);
    }

    public void closeSemester() {
        Database.getInstance().setRegistrationOpen(false);
        List<Student> students = Database.getInstance().getAllStudents();
        for (Student student : students) {
            student.updateTranscript();
            for (String courseName : student.checkFailLimits()) {
                String msg = kz.synapse.exceptions.CourseFailLimitException.messageFor(courseName);
                student.addNotification(new Notification("ACADEMIC WARNING: " + msg));
            }
            student.clearSemesterEnrollment();
        }
        addNews("Semester closed", "All marks finalized and transcripts updated.", NewsType.ANNOUNCEMENT);
        logAction("Closed the current semester");
    }

    public List<OrganizationProposal> getPendingProposals() {
        return Database.getInstance().getOrgProposals().stream()
                .filter(p -> p.getStatus() == OrganizationProposal.Status.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }

    public StudentOrganization approveProposal(OrganizationProposal proposal) {
        proposal.approve();
        Student founder = proposal.getProposer();
        StudentOrganization org = new StudentOrganization(
                proposal.getName(), proposal.getDescription(), founder);
        founder.addToOrganization(org);
        Database.getInstance().addOrganization(org);
        Database.getInstance().removeOrgProposal(proposal);
        founder.addNotification(new Notification(
                "Your organization '" + org.getName() + "' was approved! You are the Head."));
        logAction("Approved organization proposal: " + proposal.getName());
        return org;
    }

    public void rejectProposal(OrganizationProposal proposal) {
        proposal.reject();
        proposal.getProposer().addNotification(new Notification(
                "Your organization proposal '" + proposal.getName() + "' was rejected."));
        Database.getInstance().removeOrgProposal(proposal);
        logAction("Rejected organization proposal: " + proposal.getName());
    }

    public void dissolveOrganization(StudentOrganization org) {
        logAction("Dissolved organization: " + org.getName());
        Database.getInstance().removeOrganization(org);
    }

    @Override
    public String toString() {
        return String.format("ORManager{name='%s', email='%s'}", getName(), getEmail());
    }
}
