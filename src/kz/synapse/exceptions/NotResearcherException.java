package kz.synapse.exceptions;

public class NotResearcherException extends RuntimeException {
    public NotResearcherException() {
        super("Only researchers can join a research project");
    }

    public NotResearcherException(String userName) {
        super(userName + " is not a researcher and cannot join a research project");
    }
}
