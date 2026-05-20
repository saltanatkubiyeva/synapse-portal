package kz.synapse.services;

import kz.synapse.database.Database;
import kz.synapse.exceptions.AuthenticationException;
import kz.synapse.exceptions.UnauthorizedAccessException;
import kz.synapse.models.User;

import java.time.LocalDateTime;

public class AuthService {

    private static AuthService instance;
    private String currentUserId;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null)
            instance = new AuthService();
        return instance;
    }

    public synchronized void invalidateSession() {
        currentUserId = null;
    }

    public User login(String email, String password) {
        User user = Database.getInstance().findByEmail(email);

        if (user == null || !user.getPassword().equals(password))
            throw new AuthenticationException();

        if (user.isBanned())
            throw new UnauthorizedAccessException();

        this.currentUserId = user.getId();
        Database.getInstance().addUserLog(
                "[" + LocalDateTime.now() + "] "
                        + user.getClass().getSimpleName()
                        + " " + user.getName() + " logged in");
        return getCurrentUser();
    }

    public void logout() {
        User user = getCurrentUser();
        if (user != null) {
            Database.getInstance().addUserLog(
                    "[" + LocalDateTime.now() + "] "
                            + user.getClass().getSimpleName()
                            + " " + user.getName() + " logged out");
        }
        currentUserId = null;
    }

    public User getCurrentUser() {
        if (currentUserId == null)
            return null;
        User user = Database.getInstance().findById(currentUserId);
        if (user == null)
            currentUserId = null;
        return user;
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}
