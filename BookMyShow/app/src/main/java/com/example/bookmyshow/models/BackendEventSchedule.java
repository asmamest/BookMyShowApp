package com.example.bookmyshow.models;

import java.time.LocalDateTime;

public class BackendEventSchedule {
    private Long id;
    private Long eventId;
    private Long lieuId;
    private LocalDateTime dateTime;
    private LocalDateTime checkinTime;
    private boolean isSoldOut;
    private String eventTitle;
    private String lieuName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getLieuId() {
        return lieuId;
    }

    public void setLieuId(Long lieuId) {
        this.lieuId = lieuId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getLieuName() {
        return lieuName;
    }

    public void setLieuName(String lieuName) {
        this.lieuName = lieuName;
    }
}
