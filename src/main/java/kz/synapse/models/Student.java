package kz.synapse.models;

import kz.synapse.enums.School;
import java.util.List;
import java.util.Map;

public class Student extends User implements Comparable<Student> {
    private int credits;
    private double gpa;
    private String status;
    private School school;
    private int failCount;
    private List<Course> enrolledCourses;
    private Map<Course, Mark> marks;

    public Student(Long id, String name, String email, String password) {
        super(id, name, email, password);
        this.credits = 0;
        this.gpa = 0.0;
        this.status = "Active";
        this.failCount = 0;
    }

    public void registerCourse(Course course) {
    }

    public void viewMarks() {
    }

    public String getTranscript() {
        return "";
    }

    public void rateTeacher() {
    }

    public void viewTeacherInfo() {
    }

    public void joinOrganization() {
    }

    public void subscribeToJournal() {
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa);
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public Map<Course, Mark> getMarks() {
        return marks;
    }

    public void setMarks(Map<Course, Mark> marks) {
        this.marks = marks;
    }
}
