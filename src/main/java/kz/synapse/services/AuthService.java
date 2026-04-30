package kz.synapse.services;

import kz.synapse.database.Database;
import kz.synapse.exceptions.AuthenticationException;
import kz.synapse.exceptions.UnauthorizedAccessException;
import kz.synapse.models.User;

public class AuthService {

    // singleton
    private static AuthService instance;
    private User currentUser;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null)
            instance = new AuthService();
        return instance;
    }

    // login
    public User login(String email, String password) {
        User user = Database.getInstance().findByEmail(email);

        if (user == null || !user.getPassword().equals(password))
            throw new AuthenticationException();

        if (user.isBanned())
            throw new UnauthorizedAccessException();

        this.currentUser = user;
        Database.getInstance().addUserLog(
                "[" + java.time.LocalDateTime.now() + "] "
                        + user.getClass().getSimpleName()
                        + " " + user.getName() + " logged in"
        );
        return currentUser;
    }

    // logout
    public void logout() {
        if (currentUser != null) {
            Database.getInstance().addUserLog(
                    "[" + java.time.LocalDateTime.now() + "] "
                            + currentUser.getClass().getSimpleName()
                            + " " + currentUser.getName() + " logged out"
            );
            currentUser = null;
        }
    }

    // геттер
    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn()  { return currentUser != null; }
}