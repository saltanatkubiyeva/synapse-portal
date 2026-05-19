package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.*;
import kz.synapse.utils.LanguageManager;

import java.util.*;
import java.util.stream.Collectors;

public class Teacher extends Employee implements Comparable<Teacher> {

    private static final long serialVersionUID = 1L;

    private TeacherPosition position;
    private School school;
    private List<Course> courses       = new ArrayList<>();
    private List<Integer> ratingScores = new ArrayList<>();

    public Teacher(String id, String name, String email, String password,
                   Language language, TeacherPosition position, School school) {
        super(id, name, email, password, language);
        this.position = position;
        this.school   = school;
    }

    // баллы за урок — Teacher и TA могут вызывать

    public void addLessonScore(Student student, CourseOffering offering,
                               LessonScore score) {
        checkAccess(offering);
        if (!offering.isEnrolled(student))
            throw new IllegalArgumentException(
                    student.getName() + " is not enrolled in "
                            + offering.getCourse().getName());
        if (score.getPeriod() == AttestationPeriod.FINAL)
            throw new IllegalArgumentException(
                    "Use setFinalExam() for final exam. addLessonScore() is for ATT1/ATT2 only.");
        offering.getOrCreateMark(student).addLessonScore(score);
        logAction("Added lesson score for " + student.getName()
                + " in " + offering.getCourse().getName()
                + " [" + score.getPeriod() + "] " + score.getScore());
    }

    // выставляет финальный экзамен 
    public void setFinalExam(Student student, CourseOffering offering,
                             double score) {
        if (!offering.canSetFinalExam(this))
            throw new IllegalStateException(
                    "Only the lecture teacher can set the final exam score");
        if (!offering.isEnrolled(student))
            throw new IllegalArgumentException(
                    student.getName() + " is not enrolled");
        offering.getOrCreateMark(student).setFinalExam(score);
        logAction("Set final exam for " + student.getName()
                + " in " + offering.getCourse().getName()
                + ": " + score);
    }

    // посещаемость

    public void markAttendance(CourseOffering offering,
                               java.time.LocalDate date,
                               Student student, boolean isPresent) {
        checkAccess(offering);
        if (isPresent)
            offering.getAttendance().markPresent(date, student);
        else
            offering.getAttendance().markAbsent(date, student);
        logAction("Marked " + student.getName()
                + (isPresent ? " present" : " absent")
                + " in " + offering.getCourse().getName());
    }

    // просмотр студентов

    public Set<Student> viewAllStudents() {
        return Database.getInstance().getCourseOfferings().stream()
                .filter(o -> courses.contains(o.getCourse()))
                .flatMap(o -> o.getEnrolledStudents().stream())
                .collect(Collectors.toSet());
    }

    public List<Student> viewStudents(CourseOffering offering) {
        checkAccess(offering);
        return new ArrayList<>(offering.getEnrolledStudents());
    }


    public void printStudentJournal(Student student, CourseOffering offering) {
        checkAccess(offering);
        Mark mark = offering.getMark(student);
        if (mark == null) {
            System.out.println(LanguageManager.get("journal.noRecords", student.getName()));
            return;
        }
        System.out.println(LanguageManager.get("journal.header", student.getName(),
                offering.getCourse().getName()));
        mark.printJournal();
    }

    // жалобы

    public void sendComplaint(List<Student> students,
                              UrgencyLevel urgency, String reason) {
        Complaint complaint = new Complaint(this, students, urgency, reason);
        Database.getInstance().addComplaint(complaint);
        logAction("Sent complaint, urgency: " + urgency);
    }

    // отчёт по курсу

    public String generateCourseReport(CourseOffering offering) {
        if (!courses.contains(offering.getCourse())) return "Access Denied";
        StringBuilder sb = new StringBuilder(
                "Report: " + offering.getCourse().getName()
                        + " [" + offering.getSemester() + "]\n");
        sb.append(String.format("%-25s | %-4s | %-5s | %-5s | %-5s | %s%n",
                "Student", "ATT1", "ATT2", "FINAL", "TOTAL", "Grade"));
        sb.append("─".repeat(65)).append("\n");
        for (Student s : offering.getEnrolledStudents()) {
            Mark m = offering.getMark(s);
            if (m != null)
                sb.append(String.format("%-25s | %-4.1f | %-5.1f | %-5.1f | %-5.1f | %s%n",
                        s.getName(), m.getAtt1(), m.getAtt2(),
                        m.getFinalExam(), m.getTotal(), m.getLetterGrade()));
            else
                sb.append(String.format("%-25s | —%n", s.getName()));
        }
        return sb.toString();
    }

    // рейтинг

    public void addRating(int score) {
        if (score >= 1 && score <= 5) ratingScores.add(score);
    }

    public double getRating() {
        if (ratingScores.isEmpty()) return 0.0;
        return ratingScores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    private void checkAccess(CourseOffering offering) {
        if (!courses.contains(offering.getCourse()))
            throw new IllegalArgumentException(
                    "You are not assigned to " + offering.getCourse().getName());
    }

    @Override
    public int compareTo(Teacher other) { return this.getName().compareTo(other.getName()); }

    public TeacherPosition getPosition()       { return position; }
    public void setPosition(TeacherPosition p) { this.position = p; }
    public School getSchool()                  { return school; }
    public void setSchool(School school)       { this.school = school; }
    public void assignCourse(Course course) {
        if (!courses.contains(course))
            courses.add(course);
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    @Override
    public String toString() {
        return String.format("Teacher{name='%s', position=%s, school=%s, rating=%.1f}",
                getName(), position, school, getRating());
    }
}
