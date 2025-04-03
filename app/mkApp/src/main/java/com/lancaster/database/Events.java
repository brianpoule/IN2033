package com.lancaster.database;

import java.util.Date;

public class Events {
    private int eventId;
    private String type;
    private Date date;
    private int room;
    private int duration;


    public Events(int eventId, String type, Date date, int room, int duration) {
        this.eventId = eventId;
        this.type = type;
        this.date = date;
        this.room = room;
        this.duration = duration;
    }

    // Getters and setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Events{" +
                "eventId=" + eventId +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", room=" + room +
                ", duration=" + duration +
                '}';
    }
}