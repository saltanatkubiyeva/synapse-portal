package kz.synapse.exceptions;

public class CourseFailLimitException extends RuntimeException {
    public CourseFailLimitException(String courseName) {
        super("Failed " + courseName + " 3 times. Cannot register again.");
    }
}