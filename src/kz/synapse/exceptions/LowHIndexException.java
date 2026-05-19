package kz.synapse.exceptions;

public class LowHIndexException extends RuntimeException {
    public LowHIndexException() {
        super("Supervisor h-index must be >= 3");
    }
}
