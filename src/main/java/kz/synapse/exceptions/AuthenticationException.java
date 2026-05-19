package kz.synapse.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Invalid email or password");
    }
}