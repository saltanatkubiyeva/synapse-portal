package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.enums.School;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dean extends Employee implements Serializable {

    private School school;
    private List<Complaint> complaints = new ArrayList<>();

    public Dean(String id, String name, String email,
                String password, Language language,
                double salary, School school) {
        super(id, name, email, password, language, salary);
        this.school = school;
    }

    // жалобы

    public void receiveComplaint(Complaint complaint) {
        complaints.add(complaint);
        logAction("Received complaint about "
                + complaint.getAboutStudents().size()
                + " student(s), urgency: " + complaint.getUrgency());
    }

    public List<Complaint> viewComplaints() {
        logAction("Viewed complaints");
        return complaints;
    }

    // подписание запросов

    public void signRequest(EmployeeRequest request) {
        request.signByDean(this);
        logAction("Signed request from: "
                + request.getSender().getName()
                + " topic: " + request.getTopic());
    }

    public List<EmployeeRequest> viewAllRequests() {
        return Database.getInstance().getEmployeeRequests();
    }

    // геттер
    public School getSchool() { return school; }

    @Override
    public String toString() {
        return String.format(
                "Dean{name='%s', school=%s}",
                getName(), school
        );
    }
}