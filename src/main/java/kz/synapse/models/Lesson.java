package kz.synapse.models;

import kz.synapse.enums.LessonType;
import java.time.LocalDate;

public class Lesson {
    private LessonType type;
    private LocalDate date;
    private String time;
    private String room;
    private Attendance attendance;

    public Lesson(LessonType type, LocalDate date, String time, String room) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.room = room;
        this.attendance = new Attendance();
    }

    public LessonType getType() {
        return type;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}
