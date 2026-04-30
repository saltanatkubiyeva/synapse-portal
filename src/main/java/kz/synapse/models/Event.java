package kz.synapse.models;

import kz.synapse.enums.EventType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Serializable {

    private String title;
    private EventType type;
    private LocalDate date;
    private LocalTime time;
    private String room;
    private String description;

    public Event(String title, EventType type, LocalDate date,
                 LocalTime time, String room, String description) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.time = time;
        this.room = room;
        this.description = description;
    }

    // геттеры
    public String getTitle()       { return title; }
    public EventType getType()     { return type; }
    public LocalDate getDate()     { return date; }
    public LocalTime getTime()     { return time; }
    public String getRoom()        { return room; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format(
                "Event{title='%s', type=%s, date=%s, time=%s, room='%s'}",
                title, type, date, time, room
        );
    }
}