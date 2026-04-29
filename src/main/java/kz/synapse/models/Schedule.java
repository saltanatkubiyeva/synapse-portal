package kz.synapse.models;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Lesson> entries;

    public Schedule() {
        this.entries = new ArrayList<>();
    }

    public void addLesson(Lesson lesson, String day, String time) {
        entries.add(lesson);
    }

    public List<Lesson> getByDay(String day) {
        return new ArrayList<>();
    }

    public boolean checkRoomConflict() {
        return false;
    }

    public List<Lesson> getEntries() {
        return entries;
    }

    public void setEntries(List<Lesson> entries) {
        this.entries = entries;
    }
}
