package kz.synapse.models;

import kz.synapse.enums.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kz.synapse.database.Database;
public class Teacher extends Employee {

    private TeacherPosition position;
    private School school;
    private List<Course> courses = new ArrayList<>();

    private List<Integer> ratingScores = new ArrayList<>();

    public Teacher(String id, String name, String email, String password,
                   Language language, double salary, TeacherPosition position, School school) {
        super(id, name, email, password, language, salary);
        this.position = position;
        this.school = school;
    }

    public void putMark(Student student, Course course, Mark mark) {
        if (!courses.contains(course)) {
            throw new IllegalArgumentException("You don't teach this course!");
        }
        student.getMarks().put(course, mark);
    }

    public Set<Student> viewAllStudents() {
        Set<Student> allStudents = new HashSet<>();
        for (Course c : courses) {
            allStudents.addAll(c.getEnrolledStudents());
        }
        return allStudents;
    }

    public List<Student> viewStudents(Course course) {
        if (courses.contains(course))
            return new ArrayList<>(course.getEnrolledStudents());
        return new ArrayList<>();
    }

    public void sendComplaint(List<Student> students,
                              UrgencyLevel urgency,
                              String reason) {
        Complaint complaint = new Complaint(this, students, urgency, reason);
        Database.getInstance().addComplaint(complaint);

        Dean dean = Database.getInstance()
                .getDeanBySchool(this.getSchool());
        if (dean != null)
            dean.receiveComplaint(complaint);

        logAction("Sent complaint, urgency: " + urgency);
    }

    public void markAttendance(Lesson lesson, Student student, boolean isPresent) {
        if (!courses.contains(lesson.getCourse()))
            throw new IllegalArgumentException("Not your course!");

        if (isPresent)
            lesson.getAttendance().markPresent(lesson.getDate(), student);
        else
            lesson.getAttendance().markAbsent(lesson.getDate(), student);

        logAction("Marked " + student.getName()
                + (isPresent ? " present" : " absent")
                + " in " + lesson.getCourse().getName());
    }

    public void markClassAttendance(Lesson lesson, List<Student> presentStudents) {
        //отметить всех
    }

    public String generateCourseReport(Course course) {
        if (!courses.contains(course)) return "Access Denied";

        StringBuilder report = new StringBuilder("Report for " + course.getName() + ":\n");
        for (Student s : course.getEnrolledStudents()) {
            Mark m = s.getMarks().get(course);
            report.append(s.getName()).append(" - Total: ")
                    .append(m != null ? m.getTotal() : "No marks yet").append("\n");
        }
        return report.toString();
    }

    public void addRating(int score) {
        if (score >= 1 && score <= 5) {
            ratingScores.add(score);
        }
    }

    public double getRating() {
        if (ratingScores.isEmpty()) return 0.0;
        return ratingScores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public TeacherPosition getPosition() { return position; }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setPosition(TeacherPosition position) { this.position = position; }
    public List<Course> getCourses() { return courses; }
}