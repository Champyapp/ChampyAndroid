package com.example.ivan.champy_v2.model;

public class HistoryChallenge {

    boolean active;

    String level;

    String type;
    String description;
    String duration;
    String status;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HistoryChallenge(String mtype, boolean mactive, String mdescription, String mduration, String mstatus) {
        this.type = mtype;
        this.active = mactive;
        this.description = mdescription;
        this.duration = mduration;
        this.status = mstatus;
    }

}