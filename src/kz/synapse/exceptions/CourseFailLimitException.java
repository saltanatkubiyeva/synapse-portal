package kz.synapse.exceptions;

public class CourseFailLimitException extends RuntimeException {
    public CourseFailLimitException(String courseName) {
        super(messageFor(courseName));
    }

    public static String messageFor(String courseName) {
        return "Failed " + courseName + " 3 times. Cannot register again.";
    }
}
