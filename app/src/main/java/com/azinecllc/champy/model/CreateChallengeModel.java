package com.azinecllc.champy.model;

/**
 * @autor SashaKhyzhun
 * Created on 3/27/17.
 */

public class CreateChallengeModel {

    private String challengeName;
    private String challengeDescription;
    private String challengeID;
    private int challengeStreak;
    private int challengeDuration;


    public CreateChallengeModel(String name, String description, String challengeID, int duration, int streak) {
        this.challengeName = name;
        this.challengeDescription = description;
        this.challengeID = challengeID;
        this.challengeDuration = duration;
        this.challengeStreak = streak;
    }


    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    public void setChallengeID(String challengeID) {
        this.challengeID = challengeID;
    }

    public void setChallengeStreak(int challengeStreak) {
        this.challengeStreak = challengeStreak;
    }

    public void setChallengeDuration(int challengeDuration) {
        this.challengeDuration = challengeDuration;
    }


    public int getChallengeStreak() {
        return challengeStreak;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public int getChallengeDuration() {
        return challengeDuration;
    }

    public String getChallengeID() {
        return challengeID;
    }


}
