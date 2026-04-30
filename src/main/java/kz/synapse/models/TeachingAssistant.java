package kz.synapse.models;

import java.io.Serializable;

public class TeachingAssistant implements Serializable {
    private Course course;

    public TeachingAssistant(Course course) {
        this.course = course;
    }

    public void assistLesson(Lesson lesson) {
    }

    public void gradeHomework() {
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
