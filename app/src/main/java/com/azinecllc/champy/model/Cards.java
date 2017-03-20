package com.azinecllc.champy.model;

public class Cards {

    private String challengeName;
    private String challengeDays;
    private String challengeStreak;  //
    private String challengePercent; //
    private String challengeEnd;     //
    private String challengeEnemy;
    private String challengeColor;

    public Cards(String name, String days, String streak, String percent, String enemy, String color) {
        this.challengeName = name;
        this.challengeDays = days;
        this.challengeStreak = streak;
        this.challengePercent = percent;
        this.challengeEnemy = enemy;
        this.challengeColor = color;
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

    public void setChallengeEnemy(String challengeEnemy) {
        this.challengeEnemy = challengeEnemy;
    }

    public void setChallengeEnd(String challengeEnd) {
        this.challengeEnd = challengeEnd;
    }

    public void setChallengeColor(String challengeColor) {
        this.challengeColor = challengeColor;
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

    public String getChallengeEnemy() {
        return challengeEnemy;
    }

    public String getChallengeEnd() {
        return challengeEnd;
    }

    public String getChallengeColor() {
        return challengeColor;
    }
}