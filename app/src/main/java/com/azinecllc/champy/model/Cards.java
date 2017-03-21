package com.azinecllc.champy.model;

public class Cards {

    private String challengeName;
    private String challengeDays;
    private String challengeStreak;
    private String challengePercent;
    private String challengeEnd;     //
    private String challengeVersus;
    private String challengeColor;
    private String challengeStatus;
    private String challengeIsRecipient;

    public Cards(String name, String days, String streak, String percent, String enemy, String color, String status, String isRecipient) {
        this.challengeName = name;
        this.challengeDays = days;
        this.challengeStreak = streak;
        this.challengePercent = percent;
        this.challengeVersus = enemy;
        this.challengeColor = color;
        this.challengeStatus = status;
        this.challengeIsRecipient = isRecipient;
    }



    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public void setChallengeDays(String challengeDays) {
        this.challengeDays = challengeDays;
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


    public String getChallengeIsRecipient() {
        return challengeIsRecipient;
    }

    public String getChallengeStatus() {
        return challengeStatus;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getChallengeDays() {
        return challengeDays;
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
}