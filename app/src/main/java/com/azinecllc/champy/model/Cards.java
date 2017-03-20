package com.azinecllc.champy.model;

public class Cards {

    private String challengeName;
    private String challengeDays;
    private String challengeStreak;
    private String challengePercent;
    private String challengeEnemy;
    private String challengeEnd;

    public Cards(String name, String days, String streak, String percent, String enemy, String end) {
        this.challengeName = name;
        this.challengeDays = days;
        this.challengeStreak = streak;
        this.challengePercent = percent;
        this.challengeEnemy = enemy;
        this.challengeEnd = end;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeDays() {
        return challengeDays;
    }

    public void setChallengeDays(String challengeDays) {
        this.challengeDays = challengeDays;
    }

    public String getChallengeStreak() {
        return challengeStreak;
    }

    public void setChallengeStreak(String challengeStreak) {
        this.challengeStreak = challengeStreak;
    }

    public String getChallengePercent() {
        return challengePercent;
    }

    public void setChallengePercent(String challengePercent) {
        this.challengePercent = challengePercent;
    }

    public String getChallengeEnemy() {
        return challengeEnemy;
    }

    public void setChallengeEnemy(String challengeEnemy) {
        this.challengeEnemy = challengeEnemy;
    }

    public String getChallengeEnd() {
        return challengeEnd;
    }

    public void setChallengeEnd(String challengeEnd) {
        this.challengeEnd = challengeEnd;
    }
}