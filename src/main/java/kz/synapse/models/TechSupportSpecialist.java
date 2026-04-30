package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.enums.RequestStatus;
import java.util.List;
import java.util.stream.Collectors;

public class TechSupportSpecialist extends Employee {

    public TechSupportSpecialist(String id, String name, String email,
                                 String password, Language language,
                                 double salary) {
        super(id, name, email, password, language, salary);
    }

    // all requests
    public List<Request> viewRequests() {
        List<Request> requests = Database.getInstance().getTechRequests();

        // when they see new -> viewed
        requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.NEW)
                .forEach(r -> r.setStatus(RequestStatus.VIEWED));

        logAction("Viewed all tech requests");
        return requests;
    }

    // accept request
    public void accept(Request r) {
        r.setStatus(RequestStatus.ACCEPTED);
        logAction("Accepted request: " + r.getDescription());
    }

    // or reject
    public void reject(Request r) {
        r.setStatus(RequestStatus.REJECTED);
        logAction("Rejected request: " + r.getDescription());
    }

    // doneeee
    public void done(Request r) {
        r.setStatus(RequestStatus.DONE);
        logAction("Completed request: " + r.getDescription());
    }

    // new only
    public List<Request> viewNewRequests() {
        List<Request> newRequests = Database.getInstance().getTechRequests()
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.NEW)
                .peek(r -> r.setStatus(RequestStatus.VIEWED)) // <-- Меняем статус прямо в потоке!
                .collect(Collectors.toList());

        logAction("Viewed new tech requests");
        return newRequests;
    }

    // accepted only
    public List<Request> viewAcceptedRequests() {
        return Database.getInstance().getTechRequests()
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.ACCEPTED)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("TechSupportSpecialist{name='%s', email='%s'}",
                getName(), getEmail());
    }
}