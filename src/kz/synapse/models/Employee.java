package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Employee extends User {

    private static final long serialVersionUID = 1L;

    private int experienceYears;

    // личные ящики сообщений — только у сотрудников
    private List<Message> inbox  = new ArrayList<>();
    private List<Message> outbox = new ArrayList<>();

    public Employee(String id, String name, String email,
                    String password, Language language) {
        super(id, name, email, password, language);
        this.experienceYears = 0;
    }

    // direct Messages

    public void sendMessage(Employee receiver, String text) {
        if (receiver == null)
            throw new IllegalArgumentException("Receiver cannot be null");
        Message message = new Message(this, receiver, text);
        this.outbox.add(message);
        receiver.receiveMessage(message);
        logAction("Sent message to: " + receiver.getName());
    }

    protected void receiveMessage(Message message) {
        this.inbox.add(message);
    }

    public List<Message> getInbox() {
        return Collections.unmodifiableList(inbox);
    }
    public List<Message> getOutbox() {
        return Collections.unmodifiableList(outbox);
    }

    public List<Message> getUnreadMessages() {
        return inbox.stream()
                .filter(m -> !m.isRead())
                .collect(Collectors.toList());
    }

    public EmployeeRequest submitRequest(String topic, String content) {
        EmployeeRequest request = new EmployeeRequest(this, topic, content);
        Database.getInstance().addEmployeeRequest(request);
        logAction("Submitted request: " + topic);
        return request;
    }

    // геттеры / сеттеры

    public int getExperienceYears()        { return experienceYears; }
    public void addExperienceYear()        { this.experienceYears++; }
}
