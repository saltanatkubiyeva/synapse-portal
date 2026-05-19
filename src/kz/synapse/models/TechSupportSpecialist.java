package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.enums.RequestStatus;

import java.util.ArrayList;
import java.util.List;

/** техподдержка */
public class TechSupportSpecialist extends Employee {

    private static final long serialVersionUID = 1L;

    public TechSupportSpecialist(String id, String name, String email,
                                 String password, Language language) {
        super(id, name, email, password, language);
    }

    /** просмотр заявок */
    public List<Request> viewRequests() {
        List<Request> all = Database.getInstance().getTechRequests();
        for (Request r : all) {
            if (r.getStatus() == RequestStatus.NEW)
                r.setStatus(RequestStatus.VIEWED);
        }
        logAction("Viewed all tech requests (" + all.size() + ")");
        return all;
    }

    /** заявки по статусу */
    public List<Request> getByStatus(RequestStatus status) {
        List<Request> result = new ArrayList<>();
        for (Request r : Database.getInstance().getTechRequests()) {
            if (r.getStatus() == status) result.add(r);
        }
        return result;
    }

    public void accept(Request r) {
        r.setStatus(RequestStatus.ACCEPTED);
        logAction("Accepted request: " + r.getDescription());
    }

    public void reject(Request r) {
        r.setStatus(RequestStatus.REJECTED);
        logAction("Rejected request: " + r.getDescription());
    }

    public void markDone(Request r) {
        r.setStatus(RequestStatus.DONE);
        logAction("Completed request: " + r.getDescription());
    }

    @Override
    public String toString() {
        return String.format("TechSupportSpecialist{name='%s', email='%s'}",
                getName(), getEmail());
    }
}
