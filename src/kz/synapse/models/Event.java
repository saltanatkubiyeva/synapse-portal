package kz.synapse.models;

import kz.synapse.enums.EventType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/** внутреннее событие университета (бронирование аудитории, экзамен и т.д.) */
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final EventType type;
    private final LocalDate date;
    private final LocalTime time;
    private final String room;
    private final String description;

    public Event(String title, EventType type, LocalDate date,
                 LocalTime time, String room, String description) {
        this.title       = title;
        this.type        = type;
        this.date        = date;
        this.time        = time;
        this.room        = room;
        this.description = description;
    }

    public String getTitle()       { return title; }
    public EventType getType()     { return type; }
    public LocalDate getDate()     { return date; }
    public LocalTime getTime()     { return time; }
    public String getRoom()        { return room; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event e = (Event) o;
        return Objects.equals(title, e.title)
                && Objects.equals(date, e.date)
                && Objects.equals(time, e.time)
                && Objects.equals(room, e.room);
    }

    @Override
    public int hashCode() { return Objects.hash(title, date, time, room); }

    @Override
    public String toString() {
        return String.format("Event{title='%s', type=%s, date=%s, time=%s, room='%s'}",
                title, type, date, time, room);
    }
}
