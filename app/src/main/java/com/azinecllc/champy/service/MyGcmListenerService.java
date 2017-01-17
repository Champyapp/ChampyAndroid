package com.azinecllc.champy.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.FriendsActivity;
import com.azinecllc.champy.activity.HistoryActivity;
import com.azinecllc.champy.activity.PendingDuelActivity;
import com.azinecllc.champy.activity.RoleControllerActivity;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.utils.SessionManager;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.HashMap;

import static java.lang.Math.round;

public class MyGcmListenerService extends GcmListenerService {

    private final String TAG = "MyGcmListenerService";
    private SessionManager session;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        session = SessionManager.getInstance(getApplicationContext());
        if (session.isUserLoggedIn()) {
            HashMap<String, String> user;
            user = session.getUserDetails();

            String name = user.get("name");
            String message = data.getString("gcm.notification.body");
            String title = data.getString("gcm.notification.title");
            Log.d(TAG, "From: " + from + " " + name + "\nMessage: " + message);

            if (message != null && !message.toLowerCase().contains(name.toLowerCase()))
                sendNotification(message, title);
        }
    }

    /**
     * Create and show a item_friends_open notification containing the received GCM message.
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title) {
        Log.d(TAG, title);
        Intent intent = new Intent(this, RoleControllerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        switch (title) {
            case "Friend Request Confirmed":
                intent.putExtra("gcm", "friend_request_confirmed");
                notifyForFriends(intent, message);
                break;
            case "Incoming Friend Request":
                intent.putExtra("gcm", "friend_request_incoming");
                notifyForFriends(intent, message);
                break;
            case "Friend Request Removed":
                intent.putExtra("gcm", "friend_request_removed");
                notifyForFriends(intent, message);
                break;
            case "Challenge request":
                //Intent goToPendingDuels = new Intent(this, RoleControllerActivity.class);
                intent.putExtra("gcm", "challenge_request_incoming");
                notifyChallenges(intent, message);
                break;
            case "Challenge accepted":
                //Intent roleIntent = new Intent(this, RoleControllerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("gcm", "challenge_request_confirmed");
                notifyChallenges(intent, message);
                break;
            case "Win":
                //Intent historyIntent = new Intent(this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //historyIntent.putExtra("win_request", "true");
                intent.putExtra("gcm", "challenge_request_win");
                notifyChallenges(intent, message);
                break;
        }
    }


    private void notifyChallenges(Intent intent, String message) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duel_white)
                .setContentTitle("Champy")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void notifyForFriends(Intent intent, String message) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.nav_friends)
                .setContentTitle("Champy")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



//    module.exports = {
//        challengeRequest : {
//            title : 'Challenge request',
//                    body : '%s sent you a challenge request'
//        },
//        challengeAccepted : {
//            title : 'Challenge accepted',
//                    body : '%s accepted your challenge request. Let the button_duel begins.'
//        },
//        challengesForToday : {
//            title : 'Challenges for today',
//                    body : 'Hey. You have some ic_score_prog for today. Don\'t forget to complete them.'
//        },
//        friendRequest : {
//            title : 'Incoming Friend Request',
//                    body : '%s want to add you as a friend'
//        },
//        acceptedFriendRequest :{
//            title: 'Friend Request Confirmed',
//                    body: '%s has just accepted your friend request'
//        },
//        declinedFriendRequest :{
//            title: 'Friend Request Removed',
//                    body: '%s has just removed you from friends'
//        },
//        submitForApprove : {
//            title : 'Submit for approve',
//                    body : '%s completed his/her part of the button_duel for today. Please, approve it.'
//        },
//        approved : {
//            title : 'Approved',
//                    body : '%s just approved your today\'s performance.'
//        },
//        win : {
//            title : 'Win',
//                    body : 'Congratulations! You just won the button_duel against %s'
//        },
//    };

}