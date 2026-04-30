package kz.synapse.models;

import kz.synapse.enums.CourseType;
import kz.synapse.enums.School;
import kz.synapse.enums.SemesterType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Course implements Serializable {

    private String courseCode;
    private String name;
    private int credits;
    private CourseType defaultType;
    private int maxStudents;

    private School targetSchool;
    private int targetYear;
    private SemesterType semester;

    private Set<Course> prerequisites = new HashSet<>();
    private Set<Teacher> teachers = new HashSet<>();
    private Set<Student> enrolledStudents = new HashSet<>();

    public Course(String courseCode, String name, int credits,
                  CourseType defaultType, int maxStudents) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
        this.defaultType = defaultType;
        this.maxStudents = maxStudents;
    }

    public CourseType getCourseTypeFor(Student student) {
        if (this.targetSchool == null
                || student.getSchool() == this.targetSchool) {
            return this.defaultType;
        }

        if (this.defaultType == CourseType.MAJOR) {
            return CourseType.FREE_ELECTIVE;
        }

        return this.defaultType;
    }

    // вызываются менеджерами

    public void addStudent(Student student) {
        if (!hasAvailableSpots())
            throw new IllegalStateException(
                    "Course " + name + " is full"
            );
        enrolledStudents.add(student);
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public boolean hasAvailableSpots() {
        return enrolledStudents.size() < maxStudents;
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
    }

    public void addPrerequisite(Course course) {
        prerequisites.add(course);
    }

    public boolean hasPrerequisite(Course course) {
        return prerequisites.contains(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }

    @Override
    public String toString() {
        return String.format(
                "Course{code='%s', name='%s', credits=%d, " +
                        "type=%s, semester=%s, school=%s, spots=%d/%d}",
                courseCode, name, credits, defaultType,
                semester, targetSchool,
                enrolledStudents.size(), maxStudents
        );
    }

    public String getCourseCode()              { return courseCode; }
    public String getName()                    { return name; }
    public int getCredits()                    { return credits; }
    public CourseType getDefaultType()         { return defaultType; }
    public int getMaxStudents()                { return maxStudents; }
    public School getTargetSchool()            { return targetSchool; }
    public int getTargetYear()                 { return targetYear; }
    public SemesterType getSemester()          { return semester; }
    public Set<Course> getPrerequisites()      { return prerequisites; }
    public Set<Teacher> getTeachers()          { return teachers; }
    public Set<Student> getEnrolledStudents()  { return enrolledStudents; }

    public void setTargetSchool(School s)      { this.targetSchool = s; }
    public void setTargetYear(int year)        { this.targetYear = year; }
    public void setSemester(SemesterType s)    { this.semester = s; }
    public void setMaxStudents(int max)        { this.maxStudents = max; }
    public void setDefaultType(CourseType t)   { this.defaultType = t; }
}