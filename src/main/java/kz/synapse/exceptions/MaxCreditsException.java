package kz.synapse.exceptions;

public class MaxCreditsException extends RuntimeException {
    public MaxCreditsException()
    {
        super("Student cannot exceed 21 credits.");
    }
}
