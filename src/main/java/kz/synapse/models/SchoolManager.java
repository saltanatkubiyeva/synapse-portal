package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SchoolManager extends Manager {
    private School school;

    public SchoolManager(String id, String name, String email,
                         String password, Language language,
                         double salary, School school) {
        super(id, name, email, password, language, salary, ManagerType.SCHOOL);
        this.school = school;
    }

    public void assignCourseToTeacher(Teacher teacher, Course course) {
        teacher.getCourses().add(course);
        course.getTeachers().add(teacher);
        logAction("Assigned " + course.getName() + " to " + teacher.getName());
    }

    public void removeCourseFromTeacher(Teacher teacher, Course course) {
        teacher.getCourses().remove(course);
        course.getTeachers().remove(teacher);
        logAction("Removed " + course.getName() + " from " + teacher.getName());
    }

    public void addCourseForRegistration(Course course, School major,
                                         int yearOfStudy, SemesterType semester) {
        course.setTargetSchool(major);
        course.setTargetYear(yearOfStudy);
        course.setSemester(semester);
        Database.getInstance().addAvailableCourse(course);
        logAction("Opened: " + course.getName()
                + " for " + major + " year " + yearOfStudy);
    }

    @Override
    public List<Student> viewStudents(Comparator<Student> c) {
        return Database.getInstance().getAllStudents()
                .stream()
                .filter(s -> s.getSchool() == this.school)
                .sorted(c)
                .collect(Collectors.toList());
    }

    // their school only
    @Override
    public List<Teacher> viewTeachers(Comparator<Teacher> c) {
        return Database.getInstance().getAllTeachers()
                .stream()
                .filter(t -> t.getSchool() == this.school)
                .sorted(c)
                .collect(Collectors.toList());
    }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    @Override
    public String toString() {
        return String.format("SchoolManager{name='%s', school=%s}", getName(), school);
    }}