package kz.synapse.models;

import kz.synapse.enums.LessonType;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LessonSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String room;
    private final LessonType type;
    private final Teacher teacher;
    private final int maxStudents;
    private final Set<Student> enrolledStudents = new HashSet<>();

    public LessonSlot(DayOfWeek dayOfWeek, LocalTime startTime,
                      LocalTime endTime, String room,
                      LessonType type, Teacher teacher, int maxStudents) {
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException("endTime must be after startTime");
        if (maxStudents <= 0)
            throw new IllegalArgumentException("maxStudents must be > 0");
        this.dayOfWeek   = dayOfWeek;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.room        = room;
        this.type        = type;
        this.teacher     = teacher;
        this.maxStudents = maxStudents;
    }

    // запись студента в слот

    public boolean hasSpots() {
        return enrolledStudents.size() < maxStudents;
    }

    public void enrollStudent(Student student) {
        if (!hasSpots())
            throw new IllegalStateException(
                    "No spots left in slot: " + this);
        enrolledStudents.add(student);
    }

    public void unenrollStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public boolean isEnrolled(Student student) {
        return enrolledStudents.contains(student);
    }

    // конфликт времени

    public boolean overlapsWith(LessonSlot other) {
        if (this.dayOfWeek != other.dayOfWeek) return false;
        return this.startTime.isBefore(other.endTime)
                && this.endTime.isAfter(other.startTime);
    }

    // геттеры

    public DayOfWeek getDayOfWeek()          { return dayOfWeek; }
    public LocalTime getStartTime()          { return startTime; }
    public LocalTime getEndTime()            { return endTime; }
    public String getRoom()                  { return room; }
    public LessonType getType()              { return type; }
    public Teacher getTeacher()              { return teacher; }
    public int getMaxStudents()              { return maxStudents; }
    public int getCurrentStudents()          { return enrolledStudents.size(); }
    public Set<Student> getEnrolledStudents() {
        return Collections.unmodifiableSet(enrolledStudents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonSlot)) return false;
        LessonSlot s = (LessonSlot) o;
        return dayOfWeek == s.dayOfWeek
                && Objects.equals(startTime, s.startTime)
                && Objects.equals(room, s.room);
    }

    @Override
    public int hashCode() { return Objects.hash(dayOfWeek, startTime, room); }

    @Override
    public String toString() {
        return String.format("[%s] %s %s-%s | Room: %s | %s | %d/%d seats",
                type, dayOfWeek, startTime, endTime,
                room, teacher.getName(),
                enrolledStudents.size(), maxStudents);
    }
}
