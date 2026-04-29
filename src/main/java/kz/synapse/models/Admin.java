package kz.synapse.models;

import kz.synapse.enums.School;

public class Admin extends Employee {

    public Admin(Long id, String name, String email, String password, double salary, School school) {
        super(id, name, email, password, "Admin", salary, school);
    }

    public void manageUsers() {
    }

    public void addUser() {
    }

    public void removeUser() {
    }

    public void updateUser() {
    }

    public void viewLogs() {
    }

    public void banUser() {
    }

    public void resetPassword() {
    }

    public void manageRoles() {
    }
}
