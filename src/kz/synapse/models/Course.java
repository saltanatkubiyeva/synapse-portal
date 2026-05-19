package kz.synapse.models;

import kz.synapse.enums.CourseType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Course implements Serializable, Comparable<Course> {

    private static final long serialVersionUID = 1L;

    private final String courseCode;
    private final String name;
    private final int credits;          
    private final int lecturesPerWeek;  
    private final int practicesPerWeek; 
    private CourseType defaultType;
    private final Set<Course> prerequisites = new HashSet<>();

    public Course(String courseCode, String name,
                  int lecturesPerWeek, int practicesPerWeek,
                  CourseType defaultType) {
        this.courseCode       = courseCode;
        this.name             = name;
        this.lecturesPerWeek  = lecturesPerWeek;
        this.practicesPerWeek = practicesPerWeek;
        this.credits          = lecturesPerWeek + practicesPerWeek;
        this.defaultType      = defaultType;
    }

    public void addPrerequisite(Course c)         { prerequisites.add(c); }
    public boolean hasPrerequisite(Course c)      { return prerequisites.contains(c); }
    public Set<Course> getPrerequisites()         { return prerequisites; }

    // геттеры

    public String getCourseCode()            { return courseCode; }
    public String getName()                  { return name; }
    public int getCredits()                  { return credits; }
    public int getLecturesPerWeek()          { return lecturesPerWeek; }
    public int getPracticesPerWeek()         { return practicesPerWeek; }
    public CourseType getDefaultType()       { return defaultType; }
    public void setDefaultType(CourseType t) { this.defaultType = t; }

    @Override
    public int compareTo(Course other) {
        return this.courseCode.compareTo(other.courseCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return Objects.equals(courseCode, ((Course) o).courseCode);
    }

    @Override
    public int hashCode() { return Objects.hash(courseCode); }

    @Override
    public String toString() {
        return String.format(
                "Course{code='%s', name='%s', credits=%d (%dL+%dP), type=%s}",
                courseCode, name, credits,
                lecturesPerWeek, practicesPerWeek, defaultType);
    }
}
