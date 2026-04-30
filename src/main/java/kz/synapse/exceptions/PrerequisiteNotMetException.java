package kz.synapse.exceptions;

public class PrerequisiteNotMetException extends RuntimeException {
    public PrerequisiteNotMetException(String courseName) {
        super("Prerequisites not met for: " + courseName);
    }
}
