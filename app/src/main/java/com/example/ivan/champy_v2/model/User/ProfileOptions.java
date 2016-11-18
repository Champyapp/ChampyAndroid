
package com.example.ivan.champy_v2.model.user;

import java.util.HashMap;
import java.util.Map;

public class ProfileOptions {

    private Boolean joinedChampy;
    private Boolean friendRequests;
    private Boolean challengeConfirmation;
    private Boolean challengeEnd;
    private Integer reminderTime;
    private Boolean challengesForToday;
    private Boolean acceptedYourChallenge;
    private Boolean newChallengeRequests;
    private Boolean pushNotifications;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Boolean getChallengeEnd() {
        return challengeEnd;
    }

    public Boolean getAcceptedYourChallenge() {
        return acceptedYourChallenge;
    }

    public Boolean getNewChallengeRequests() {
        return newChallengeRequests;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setChallengeEnd(Boolean challengeEnd) {
        this.challengeEnd = challengeEnd;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


    // TODO: 06.10.2016 Delete later
    public Boolean getJoinedChampy() {
        return joinedChampy;
    }

    public Boolean getFriendRequests() {
        return friendRequests;
    }

    public Boolean getChallengeConfirmation() {
        return challengeConfirmation;
    }

    public Integer getReminderTime() {
        return reminderTime;
    }

    public Boolean getChallengesForToday() {
        return challengesForToday;
    }


    public void setJoinedChampy(Boolean joinedChampy) {
        this.joinedChampy = joinedChampy;
    }

    public void setFriendRequests(Boolean friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void setChallengeConfirmation(Boolean challengeConfirmation) {
        this.challengeConfirmation = challengeConfirmation;
    }

    public void setReminderTime(Integer reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setChallengesForToday(Boolean challengesForToday) {
        this.challengesForToday = challengesForToday;
    }

    public void setAcceptedYourChallenge(Boolean acceptedYourChallenge) {
        this.acceptedYourChallenge = acceptedYourChallenge;
    }

    public void setNewChallengeRequests(Boolean newChallengeRequests) {
        this.newChallengeRequests = newChallengeRequests;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

}
