package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import kz.synapse.utils.LanguageManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    public Admin(String id, String name, String email,
                 String password, Language language) {
        super(id, name, email, password, language);
    }

    // управление юзерами

    public void addUser(User user) {
        Database.getInstance().addUser(user);
        logAdminAction("Added user: " + user.getName() + " [" + user.getId() + "]");
    }

    public void removeUser(User user) {
        Database.getInstance().removeUser(user);
        logAdminAction("Removed user: " + user.getName() + " [" + user.getId() + "]");
    }

    public void updateUser(User user, String newName, String newEmail) {
        if (newName  != null) user.setName(newName);
        if (newEmail != null) user.setEmail(newEmail);
        Database.getInstance().updateUser(user);
        logAdminAction("Updated user: " + user.getId());
    }

    public void banUser(User user) {
        user.setBanned(true);
        if (user instanceof Student)
            ((Student) user).setStatus("Banned");
        Database.getInstance().updateUser(user);
        logAdminAction("Banned user: " + user.getName());
    }

    public void unbanUser(User user) {
        user.setBanned(false);
        if (user instanceof Student)
            ((Student) user).setStatus("Active");
        Database.getInstance().updateUser(user);
        logAdminAction("Unbanned user: " + user.getName());
    }

    public void resetPassword(User user) {
        String temp = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(temp);
        logAdminAction("Reset password for: " + user.getName());
        System.out.println(LanguageManager.get("admin.tempPassword", user.getName(), temp));
    }

    public List<String> viewLogs() {
        return Database.getInstance().getAllLogs();
    }

    public List<String> viewSystemLogs() {
        return Database.getInstance().getSystemLogs();
    }

    public List<String> viewUserLogs() {
        return Database.getInstance().getUserLogs();
    }

    // просмотр пользователей

    public List<User> viewAllUsers() {
        return Database.getInstance().getUsers();
    }

    private void logAdminAction(String action) {
        String entry = String.format("[%s] ADMIN %s: %s",
                LocalDateTime.now(), getName(), action);
        Database.getInstance().addLog(entry);
    }

    @Override
    public String toString() {
        return String.format("Admin{name='%s', email='%s'}", getName(), getEmail());
    }
}
