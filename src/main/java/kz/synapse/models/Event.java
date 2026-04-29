package kz.synapse.models;

import kz.synapse.enums.EventType;
import java.time.LocalDate;

public class Event {
    private EventType type;
    private LocalDate date;
    private String description;
    private String room;

    public Event(EventType type, LocalDate date, String description, String room) {
        this.type = type;
        this.date = date;
        this.description = description;
        this.room = room;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
