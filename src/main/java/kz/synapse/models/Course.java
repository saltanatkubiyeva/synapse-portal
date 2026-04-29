package kz.synapse.models;

import kz.synapse.enums.CourseType;
import kz.synapse.enums.School;
import kz.synapse.services.CourseStatistics;
import java.util.List;

public class Course {
    private String name;
    private int credits;
    private CourseType type;
    private School school;
    private int year;
    private int maxStudents;
    private List<Course> prerequisites;
    private List<Teacher> teachers;
    private List<Lesson> lessons;
    private List<Student> enrolledStudents;

    public Course(String name, int credits, CourseType type, School school, int year, int maxStudents) {
        this.name = name;
        this.credits = credits;
        this.type = type;
        this.school = school;
        this.year = year;
        this.maxStudents = maxStudents;
    }

    public CourseStatistics getCourseStatistics() {
        return new CourseStatistics();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
}
