package kz.synapse.models;

import kz.synapse.enums.School;
import java.util.List;

public abstract class Employee extends User {
    protected String position;
    protected double salary;
    protected School school;

    public Employee(Long id, String name, String email, String password, String position, double salary, School school) {
        super(id, name, email, password);
        this.position = position;
        this.salary = salary;
        this.school = school;
    }

    public void viewSchedule() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
