 package kz.synapse.models;

import kz.synapse.enums.LessonType; // LECTURE/PRACTICE
import java.time.LocalDate;
import java.time.LocalTime;

public class Lesson {

    private Course course;
    private Teacher teacher;
    private LessonType type;
    private String room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Attendance attendance;

    public Lesson(Course course, Teacher teacher, LessonType type,
                  LocalDate date, LocalTime startTime, LocalTime endTime, String room) {
        this.course = course;
        this.teacher = teacher;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;

        this.attendance = new Attendance(course);
    }

    public Course getCourse() { return course; }
    public Teacher getTeacher() { return teacher; }
    public LessonType getType() { return type; }

    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    public String getRoom() { return room; }
    public Attendance getAttendance() { return attendance; }

    @Override
    public String toString() {
        return String.format("Lesson{%s - %s, Date: %s, Time: %s-%s, Room: %s, Teacher: %s}",
                course.getCourseCode(), type, date, startTime, endTime, room, teacher.getName());
    }
}