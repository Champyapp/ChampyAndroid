package com.azinecllc.champy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHLoadUserProgressBarInfo;
import com.azinecllc.champy.interfaces.ActiveInProgress;
import com.azinecllc.champy.model.active_in_progress.Challenge;
import com.azinecllc.champy.model.active_in_progress.Datum;
import com.azinecllc.champy.model.active_in_progress.Recipient;
import com.azinecllc.champy.model.active_in_progress.Sender;
import com.azinecllc.champy.utils.Constants;
import com.azinecllc.champy.utils.SessionManager;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static java.lang.Math.round;

public class MyGcmListenerService extends GcmListenerService {

    private final String TAG = "MyGcmListenerService";
    private SessionManager sessionManager;

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

        sessionManager = SessionManager.getInstance(getApplicationContext());
        if (sessionManager.isUserLoggedIn()) {
            HashMap<String, String> user;
            user = sessionManager.getUserDetails();

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
        Intent friendsIntent = new Intent(this, FriendsActivity.class);
        friendsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ChallengeController cc;

        switch (title) {
            case "Friend Request Confirmed":
                friendsIntent.putExtra("friend_request", "friend_request_confirmed");
                notifyForFriends(friendsIntent, message);
                break;
            case "Incoming Friend Request":
                friendsIntent.putExtra("friend_request", "incoming_friend_request");
                notifyForFriends(friendsIntent, message);
                break;
            case "Friend Request Removed":
                friendsIntent.putExtra("friend_request", "friend_request_removed");
                notifyForFriends(friendsIntent, message);
                break;
            case "Challenge request":
                Intent goToPendingDuels = new Intent(MyGcmListenerService.this, PendingDuelActivity.class);
                cc  = new ChallengeController();
                cc.refreshCardsForPendingDuel(goToPendingDuels);
                notifyChallenges(goToPendingDuels, message);
                break;
            case "Challenge accepted":
                Intent roleIntent = new Intent(MyGcmListenerService.this, RoleControllerActivity.class);
                roleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                notifyChallenges(roleIntent, message);
                break;
            case "Win":
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                historyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                historyIntent.putExtra("win_request", "true");
                cc = new ChallengeController();
                cc.refreshCardsForPendingDuel(historyIntent);
                //generateCardsForMainActivity(sessionManager.getToken(), sessionManager.getUserId());

                notifyChallenges(historyIntent, message);
                break;
        }
    }


    private void notifyChallenges(Intent intent, String message) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(MyGcmListenerService.this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
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