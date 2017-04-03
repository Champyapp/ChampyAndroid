package com.azinecllc.champy.model;

public class CardChallenges {

    private String challengeName;
    private String challengeDuration;
    private String challengeStreak;
    private String challengePercent;
    private String challengeEnd;     //
    private String challengeVersus;
    private String challengeColor;
    private String challengeStatus;
    private String challengeIsRecipient;
    private String challengeType;
    private String currentDay;

    public CardChallenges(String name, String duration, String streak, String percent, String versus,
                          String color, String status, String isRecipient, String type, String curDay) {
        this.challengeName = name;
        this.challengeDuration = duration;
        this.challengeStreak = streak;
        this.challengePercent = percent;
        this.challengeVersus = versus;
        this.challengeColor = color;
        this.challengeStatus = status;
        this.challengeIsRecipient = isRecipient;
        this.challengeType = type;
        this.currentDay = curDay;
    }


    public String getChallengeType() {
        return challengeType;
    }

    public String getChallengeIsRecipient() {
        return challengeIsRecipient;
    }

    public String getChallengeStatus() {
        return challengeStatus;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getChallengeDuration() {
        return challengeDuration;
    }

    public String getChallengeStreak() {
        return challengeStreak;
    }

    public String getChallengePercent() {
        return challengePercent;
    }

    public String getChallengeVersus() {
        return challengeVersus;
    }

    public String getChallengeEnd() {
        return challengeEnd;
    }

    public String getChallengeColor() {
        return challengeColor;
    }

    public String getCurrentDay() {
        return currentDay;
    }


    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public void setChallengeDuration(String challengeDuration) {
        this.challengeDuration = challengeDuration;
    }

    public void setChallengeStreak(String challengeStreak) {
        this.challengeStreak = challengeStreak;
    }

    public void setChallengePercent(String challengePercent) {
        this.challengePercent = challengePercent;
    }

    public void setChallengeVersus(String challengeVersus) {
        this.challengeVersus = challengeVersus;
    }

    public void setChallengeEnd(String challengeEnd) {
        this.challengeEnd = challengeEnd;
    }

    public void setChallengeColor(String challengeColor) {
        this.challengeColor = challengeColor;
    }

    public void setChallengeStatus(String challengeStatus) {
        this.challengeStatus = challengeStatus;
    }

    public void setChallengeIsRecipient(String challengeIsRecipient) {
        this.challengeIsRecipient = challengeIsRecipient;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

}