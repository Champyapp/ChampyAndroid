package com.azinecllc.champy.model;

public class HistoryChallenge {

    private boolean active;
    private String goal;
    private String challengeName;
    private String type;
    private String description;
    private String duration;
    private String status;
    private String versus;
    private String recipient;
    private String constDuration;

    public String getConstDuration() {
        return constDuration;
    }

    public void setConstDuration(String constDuration) {
        this.constDuration = constDuration;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getVersus() {
        return versus;
    }

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

    public HistoryChallenge(String mType, boolean mActive, String mDescription, String mDuration, String mStatus,
                            String goal, String challengeName, String versus, String recipient, String constDuration) {
        this.type = mType;
        this.active = mActive;
        this.description = mDescription;
        this.duration = mDuration;
        this.status = mStatus;
        this.goal = goal;
        this.challengeName = challengeName;
        this.versus = versus;
        this.recipient = recipient;
        this.constDuration = constDuration;
    }

}