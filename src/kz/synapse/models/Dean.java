package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.enums.School;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Dean extends Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private final School school;

    public Dean(String id, String name, String email,
                String password, Language language, School school) {
        super(id, name, email, password, language);
        this.school = school;
    }

    // жалобы — читаем из Database, фильтруем по своей школе

    /** возвращает все жалобы, отправленные преподавателями этой школы */
    public List<Complaint> viewComplaints() {
        logAction("Viewed complaints");
        return Database.getInstance().getComplaints().stream()
                .filter(c -> c.getSender().getSchool() == this.school)
                .collect(Collectors.toList());
    }

    // employeeRequest — подписание

    /** подписывает официальную заявку сотрудника */
    public void signRequest(EmployeeRequest request) {
        request.signByDean(this);
        logAction("Signed request from: " + request.getSender().getName()
                + ", topic: " + request.getTopic());
    }

    /** все заявки сотрудников — для просмотра деканом перед подписью */
    public List<EmployeeRequest> viewAllRequests() {
        return Database.getInstance().getEmployeeRequests();
    }

    public School getSchool() { return school; }

    @Override
    public String toString() {
        return String.format("Dean{name='%s', school=%s}", getName(), school);
    }
}
