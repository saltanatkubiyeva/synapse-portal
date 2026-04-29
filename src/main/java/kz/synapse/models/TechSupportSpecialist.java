package kz.synapse.models;

import kz.synapse.enums.School;
import java.util.List;

public class TechSupportSpecialist extends Employee {
    private List<Request> requests;

    public TechSupportSpecialist(Long id, String name, String email, String password, double salary, School school) {
        super(id, name, email, password, "TechSupportSpecialist", salary, school);
    }

    public void viewRequests() {
    }

    public void accept(Request request) {
    }

    public void reject(Request request) {
    }

    public void done(Request request) {
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
