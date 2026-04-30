package kz.synapse.models;

import kz.synapse.enums.Language;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Employee extends User {
    protected double salary;
    private int experienceYears;

    private List<Message> inbox = new ArrayList<>();
    private List<Message> outbox = new ArrayList<>();

    public Employee(String id, String name, String email, String password, Language language, double salary) {
        super(id, name, email, password, language);
        this.salary = salary;
        this.experienceYears = 0;
    }

    public void sendMessage(Employee receiver, String text) {
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null");
        }

        Message message = new Message(this, receiver, text);

        this.outbox.add(message);

        receiver.receiveMessage(message);
    }

    protected void receiveMessage(Message message) {
        this.inbox.add(message);
    }
    public List<Message> getInbox() { return inbox; }
    public List<Message> getOutbox() { return outbox; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public int getExperienceYears() { return experienceYears; }
    public void addExperienceYear() { this.experienceYears++; }
}
