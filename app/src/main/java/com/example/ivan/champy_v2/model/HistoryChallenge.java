package com.example.ivan.champy_v2.model;

public class HistoryChallenge {

    boolean active;

    String level;
    String goal;
    String challengeName;
    String type;
    String description;
    String duration;
    String status;


    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String name) {
        this.challengeName = name;
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

    public HistoryChallenge(String mtype, boolean mactive, String mdescription, String mduration, String mstatus, String goal, String challengeName) {
        this.type = mtype;
        this.active = mactive;
        this.description = mdescription;
        this.duration = mduration;
        this.status = mstatus;
        this.goal = goal;
        this.challengeName = challengeName;
    }

}