
package com.example.ivan.champy_v2.model.active_in_progress;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
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

    /**
     * 
     * @return
     *     The joinedChampy
     */
    public Boolean getJoinedChampy() {
        return joinedChampy;
    }

    /**
     * 
     * @param joinedChampy
     *     The joinedChampy
     */
    public void setJoinedChampy(Boolean joinedChampy) {
        this.joinedChampy = joinedChampy;
    }

    /**
     * 
     * @return
     *     The friendRequests
     */
    public Boolean getFriendRequests() {
        return friendRequests;
    }

    /**
     * 
     * @param friendRequests
     *     The friendRequests
     */
    public void setFriendRequests(Boolean friendRequests) {
        this.friendRequests = friendRequests;
    }

    /**
     * 
     * @return
     *     The challengeConfirmation
     */
    public Boolean getChallengeConfirmation() {
        return challengeConfirmation;
    }

    /**
     * 
     * @param challengeConfirmation
     *     The challengeConfirmation
     */
    public void setChallengeConfirmation(Boolean challengeConfirmation) {
        this.challengeConfirmation = challengeConfirmation;
    }

    /**
     * 
     * @return
     *     The challengeEnd
     */
    public Boolean getChallengeEnd() {
        return challengeEnd;
    }

    /**
     * 
     * @param challengeEnd
     *     The challengeEnd
     */
    public void setChallengeEnd(Boolean challengeEnd) {
        this.challengeEnd = challengeEnd;
    }

    /**
     * 
     * @return
     *     The reminderTime
     */
    public Integer getReminderTime() {
        return reminderTime;
    }

    /**
     * 
     * @param reminderTime
     *     The reminderTime
     */
    public void setReminderTime(Integer reminderTime) {
        this.reminderTime = reminderTime;
    }

    /**
     * 
     * @return
     *     The challengesForToday
     */
    public Boolean getChallengesForToday() {
        return challengesForToday;
    }

    /**
     * 
     * @param challengesForToday
     *     The challengesForToday
     */
    public void setChallengesForToday(Boolean challengesForToday) {
        this.challengesForToday = challengesForToday;
    }

    /**
     * 
     * @return
     *     The acceptedYourChallenge
     */
    public Boolean getAcceptedYourChallenge() {
        return acceptedYourChallenge;
    }

    /**
     * 
     * @param acceptedYourChallenge
     *     The acceptedYourChallenge
     */
    public void setAcceptedYourChallenge(Boolean acceptedYourChallenge) {
        this.acceptedYourChallenge = acceptedYourChallenge;
    }

    /**
     * 
     * @return
     *     The newChallengeRequests
     */
    public Boolean getNewChallengeRequests() {
        return newChallengeRequests;
    }

    /**
     * 
     * @param newChallengeRequests
     *     The newChallengeRequests
     */
    public void setNewChallengeRequests(Boolean newChallengeRequests) {
        this.newChallengeRequests = newChallengeRequests;
    }

    /**
     * 
     * @return
     *     The pushNotifications
     */
    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    /**
     * 
     * @param pushNotifications
     *     The pushNotifications
     */
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
