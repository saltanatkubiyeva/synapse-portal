package kz.synapse.models;

import kz.synapse.enums.LessonType;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule implements Serializable {

    public static class ScheduleEntry implements Serializable {
        private Course course;
        private LessonType lessonType;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;
        private String room;

        public ScheduleEntry(Course course, LessonType lessonType,
                             DayOfWeek day, LocalTime startTime,
                             LocalTime endTime, String room) {
            this.course = course;
            this.lessonType = lessonType;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.room = room;
        }

        public Course getCourse()         { return course; }
        public LessonType getLessonType() { return lessonType; }
        public DayOfWeek getDay()         { return day; }
        public LocalTime getStartTime()   { return startTime; }
        public LocalTime getEndTime()     { return endTime; }
        public String getRoom()           { return room; }

        @Override
        public String toString() {
            return String.format(
                    "%s | %s | %s | %s-%s | Room: %s",
                    day, course.getName(), lessonType,
                    startTime, endTime, room
            );
        }
    }

    private List<ScheduleEntry> entries = new ArrayList<>();

    public void addLesson(Course course, LessonType type,
                          DayOfWeek day, LocalTime start,
                          LocalTime end, String room) {
        if (checkRoomConflict(day, start, end, room))
            throw new IllegalArgumentException(
                    "Room conflict! " + room + " is already booked at this time."
            );
        entries.add(new ScheduleEntry(course, type, day, start, end, room));
    }

    public List<ScheduleEntry> getByDay(DayOfWeek day) {
        return entries.stream()
                .filter(e -> e.getDay() == day)
                .collect(Collectors.toList());
    }

    public List<ScheduleEntry> getByCourse(Course course) {
        return entries.stream()
                .filter(e -> e.getCourse().equals(course))
                .collect(Collectors.toList());
    }

    public List<ScheduleEntry> getByRoom(String room) {
        return entries.stream()
                .filter(e -> e.getRoom().equals(room))
                .collect(Collectors.toList());
    }

    public boolean checkRoomConflict(DayOfWeek day, LocalTime start,
                                     LocalTime end, String room) {
        return entries.stream()
                .filter(e -> e.getDay() == day
                        && e.getRoom().equals(room))
                .anyMatch(e -> start.isBefore(e.getEndTime())
                        && end.isAfter(e.getStartTime()));
    }

    public void removeLesson(Course course, DayOfWeek day) {
        entries.removeIf(e -> e.getCourse().equals(course)
                && e.getDay() == day);
    }

    public List<ScheduleEntry> getAllEntries() { return entries; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== Schedule ===\n");
        for (DayOfWeek day : DayOfWeek.values()) {
            List<ScheduleEntry> dayEntries = getByDay(day);
            if (!dayEntries.isEmpty()) {
                sb.append(day).append(":\n");
                dayEntries.forEach(e ->
                        sb.append("  ").append(e).append("\n")
                );
            }
        }
        return sb.toString();
    }
}