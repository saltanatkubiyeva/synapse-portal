package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.Language;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee {

    public Admin(String id, String name, String email, String password, Language language, double salary) {
        super(id, name, email, password, language, salary);
    }

    // manage users
    public void addUser(User user) {
        Database.getInstance().addUser(user);
        logAdminAction("Added new user: " + user.getId());
    }

    public void removeUser(User user) {
        Database.getInstance().removeUser(user);
        logAdminAction("Removed user: " + user.getId());
    }

    public void updateUser(User user, String newName, String newEmail) {
        if (newName != null) user.setName(newName);
        if (newEmail != null) user.setEmail(newEmail);

        Database.getInstance().updateUser(user);
        logAdminAction("Updated user: " + user.getId());
    }

    public void banUser(User user) {
        user.setBanned(true);

        if (user instanceof Student) {
            ((Student) user).setStatus("Banned");
        }

        Database.getInstance().updateUser(user);
        logAdminAction("Banned user: " + user.getId());
    }

    // see log
    public List<String> viewLogs() {
        return Database.getInstance().getSystemLogs();
    }

    //bonus???

    public void resetPassword(User user) {
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(tempPassword);
        logAdminAction("Reset password for user: " + user.getId());
        //like sending email
        System.out.println("Temporary password for " + user.getName() + ": " + tempPassword);
    }

    private void logAdminAction(String action) {
        String logEntry = String.format("[%s] ADMIN %s: %s",
                java.time.LocalDateTime.now(), this.getName(), action);
        Database.getInstance().addLog(logEntry);
    }
}