package com.azinecllc.champy.model.user;

import java.util.HashMap;

public class Profile_data {

    private String joinedChampy;
    private String friendRequests;
    private String challengeConfirmation;
    private String challengeEnd;
    private String reminderTime;
    private String challengesForToday;
    private String acceptedYourChallenge;
    private String newChallengeRequests;
    private String pushNotifications;

    public Profile_data(HashMap<String, String> options) {
        this.joinedChampy = options.get("joinedChampy");
        this.friendRequests = options.get("friendRequests");
        this.challengeConfirmation = options.get("challengeConfirmation");
        this.challengeEnd = options.get("challengeEnd");
        this.reminderTime = options.get("reminderTime");
        this.challengesForToday = options.get("challengesForToday");
        this.acceptedYourChallenge = options.get("acceptedYourChallenge");
        this.newChallengeRequests = options.get("newChallengeRequests");
        this.pushNotifications = options.get("pushNotifications");
    }

}
