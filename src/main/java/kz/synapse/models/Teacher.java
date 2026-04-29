package kz.synapse.models;

import kz.synapse.enums.School;
import kz.synapse.enums.TeacherPosition;
import kz.synapse.enums.UrgencyLevel;
import java.util.List;

public class Teacher extends Employee {
    private TeacherPosition position;
    private double rating;
    private List<Course> courses;

    public Teacher(Long id, String name, String email, String password, double salary, School school) {
        super(id, name, email, password, "Teacher", salary, school);
        this.rating = 0.0;
    }

    public void putMark(Student student, Mark mark) {
    }

    public void viewStudents() {
    }

    public void sendComplaint(UrgencyLevel urgencyLevel) {
    }

    public String generateCourseReport() {
        return "";
    }

    public void markAttendance(Lesson lesson) {
    }

    public void manageCourse(Course course) {
    }

    public TeacherPosition getPosition() {
        return position;
    }

    public void setPosition(TeacherPosition position) {
        this.position = position;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
