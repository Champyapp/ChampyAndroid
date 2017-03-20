package com.azinecllc.champy.model;

/**
 * @autor SashaKhyzhun
 * Created on 3/18/17.
 */

public class Card {

    private String challengeName;
    private String challengeStreak;
    private String challengeDay;
    private String completePercent;

    public Card(String challengeName, String challengeStreak, String challengeDay, String completePercent) {
        this.challengeName = challengeName;
        this.challengeStreak = challengeStreak;
        this.challengeDay = challengeDay;
        this.completePercent = completePercent;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeStreak() {
        return challengeStreak;
    }

    public void setChallengeStreak(String challengeStreak) {
        this.challengeStreak = challengeStreak;
    }

    public String getChallengeDay() {
        return challengeDay;
    }

    public void setChallengeDay(String challengeDay) {
        this.challengeDay = challengeDay;
    }

    public String getCompletePercent() {
        return completePercent;
    }

    public void setCompletePercent(String completePercent) {
        this.completePercent = completePercent;
    }
}

