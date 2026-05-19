package kz.synapse.models;

import kz.synapse.enums.*;
import kz.synapse.exceptions.*;
import kz.synapse.services.RegistrationService;
import java.io.Serializable;
import java.util.*;

public class Student extends User implements Comparable<Student>, Serializable {

    private int credits;
    private double gpa;
    private String status;
    private School school;
    private Map<Course, Integer> courseFails = new HashMap<>();
    private List<Course> enrolledCourses = new ArrayList<>();
    private Map<Course, Mark> marks = new HashMap<>();
    private Transcript transcript;

    public Student(String id, String name, String email,
                   String password, Language language, School school) {
        super(id, name, email, password, language);
        this.school = school;
        this.credits = 0;
        this.gpa = 0.0;
        this.status = "Active";
        this.transcript = new Transcript(this);
    }

    // регистрация

    public void registerCourse(Course course) {
        new RegistrationService().register(this, course);
        logAction("Requested registration: " + course.getCourseCode());
    }

    // marks

    public void addMark(Course course, Mark mark) {
        marks.put(course, mark);
    }

    public void viewMarks() {
        if (marks.isEmpty()) {
            System.out.println("No marks yet.");
            return;
        }
        marks.forEach((course, mark) ->
                System.out.println(course.getName() + ": "
                        + mark.getLetterGrade()
                        + " (" + mark.getTotal() + ")")
        );
    }

    public String getTranscript() {
        logAction("Viewed transcript");
        return transcript.toString();
    }

    // закрытие семестра

    public void checkFailLimits() {
        for (Course course : enrolledCourses) {
            Mark mark = marks.get(course);
            if (mark == null) continue;

            if (mark.isPassed()) {
                transcript.addRecord(course, mark);
            } else {
                int fails = courseFails.getOrDefault(course, 0) + 1;
                courseFails.put(course, fails);

                getNotifications().add(new Notification(
                        "You failed: " + course.getName()
                                + " (Attempt: " + fails + ")"
                ));

                if (fails >= 3)
                    throw new CourseFailLimitException(course.getName());
            }
        }
    }

    public void updateTranscript() {
        marks.forEach((course, mark) -> {
            if (mark.isPassed())
                transcript.addRecord(course, mark);
        });
        this.gpa = transcript.calculateGpa();
        logAction("Transcript updated");
    }

    // взаимодействие

    public void rateTeacher(Teacher teacher, int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be 1-5");
        teacher.addRating(rating);
        logAction("Rated teacher: " + teacher.getName() + " → " + rating);
    }

    public void viewTeacherInfo(Teacher teacher) {
        System.out.println(teacher.toString());
    }

    public void joinOrganization(StudentOrganization org) {
        org.addMember(this);
        logAction("Joined organization: " + org.getName());
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa);
    }

    @Override
    public String toString() {
        return String.format(
                "Student{name='%s', school=%s, gpa=%.2f, credits=%d}",
                getName(), school, gpa, credits
        );
    }

    public int getCredits()                      { return credits; }
    public void setCredits(int credits)          { this.credits = credits; }
    public double getGpa()                       { return gpa; }
    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }
    public School getSchool()                    { return school; }
    public Map<Course, Integer> getCourseFails() { return courseFails; }
    public List<Course> getEnrolledCourses()     { return enrolledCourses; }
    public Map<Course, Mark> getMarks()          { return marks; }
    public Transcript getTranscriptObject()      { return transcript; }
    public Set<Course> getPassedCourses() {
        return new HashSet<>(transcript.getPassedCourses());
    }
}