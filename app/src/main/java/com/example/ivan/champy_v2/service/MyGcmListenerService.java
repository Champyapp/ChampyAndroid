package com.example.ivan.champy_v2.service;

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

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.utils.Constants;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.example.ivan.champy_v2.activity.FriendsActivity;
import com.example.ivan.champy_v2.activity.HistoryActivity;
import com.example.ivan.champy_v2.activity.PendingDuelActivity;
import com.example.ivan.champy_v2.activity.RoleControllerActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHLoadUserProgressBarInfo;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
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

import static com.example.ivan.champy_v2.utils.Constants.typeDuel;
import static com.example.ivan.champy_v2.utils.Constants.typeSelf;
import static java.lang.Math.round;

public class MyGcmListenerService extends GcmListenerService {

    // TODO: 11/23/16 Delete from this class method "isUpdated" and update "Generate" method;
    private static final String TAG = "MyGcmListenerService";

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

        final SessionManager sessionManager = new SessionManager(this);
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
        switch (title) {
            case "Friend request":
                Intent friendsIntent = new Intent(this, FriendsActivity.class);
                friendsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                friendsIntent.putExtra("friend_request", "true");
                notifyForFriends(friendsIntent, message);
                break;
            case "Challenge request":
                Intent goToPendingDuels = new Intent(MyGcmListenerService.this, PendingDuelActivity.class);
                refreshPendingDuels();
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

                CurrentUserHelper user = new CurrentUserHelper(this);
                String userId = user.getUserObjectId();
                String token  = user.getToken();
                refreshPendingDuels();
                generateCardsForMainActivity(token, userId);
                notifyChallenges(historyIntent, message);
                break;
        }
    }


    private void refreshPendingDuels() {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int clearCount = db.delete("pending_duel", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final SessionManager sessionManager = new SessionManager(this);
        final String update = "0"; //1457019726
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String userId = user.get("id");
        String token = user.get("token");

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Recipient recipient = datum.getRecipient();
                        Sender sender = datum.getSender();
                        Challenge challenge = datum.getChallenge();
                        String inProgressId = datum.get_id();
                        String challengeId = challenge.get_id();
                        String challengeStatus = datum.getStatus();
                        String challengeDescription = challenge.getDescription();
                        int challengeDuration = challenge.getDuration();

                        if (challengeStatus.equals("pending")) {
                            if (userId.equals(recipient.getId())) {
                                cv.put("recipient", "true");
                                cv.put("versus", sender.getName());
                            } else {
                                cv.put("recipient", "false");
                                cv.put("versus", recipient.getName());
                            }
                            cv.put("challenge_id", inProgressId);
                            cv.put("description", challengeDescription);
                            cv.put("duration", challengeDuration);
                            db.insert("pending_duel", null, cv);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }


    private void generateCardsForMainActivity(final String token, final String userId) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        int clearCount = db.delete("myChallenges", null, null);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, "0", token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Challenge challenge          = datum.getChallenge();
                        Recipient recipient          = datum.getRecipient();
                        Sender sender                = datum.getSender();

                        String challenge_description = challenge.getDescription();   // no smoking
                        String challenge_detail      = challenge.getDetails();       // no smoking + " during this period"
                        String challenge_status      = datum.getStatus();            // active or not
                        String challenge_id          = datum.get_id();               // im progress id
                        String challenge_type        = challenge.getType();          // 567d51c48322f85870fd931a / b / c
                        String challenge_name        = challenge.getName();          // wake up (time / self / duel
                        String challenge_updated     = isUpdated(challenge_id);      // bool check method;
                        String challenge_duration = "";
                        String constDuration = "";
                        List<Object> progress;
                        String needsToCheck;
                        String challType = (challenge_type.equals(typeSelf)) ? "Self-Improvement" : (challenge_type.equals(typeDuel)) ? "Duel" : "Wake Up";
                        String versus = (challenge_type.equals(typeDuel)) ? (userId.equals(sender.get_id()) ? recipient.getName() : sender.getName()) : "notDuel";

                        if (userId.equals(sender.get_id())) {
                            progress = datum.getSenderProgress();
                            needsToCheck = datum.getNeedsToCheckSender();
                            cv.put("recipient", "false");
                        } else {
                            progress = datum.getRecipientProgress();
                            needsToCheck = datum.getNeedsToCheckRecipient();
                            cv.put("recipient", "true");
                        }

                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int begin = datum.getBegin();
                            int days = round((end - Constants.unixTime) / 86400);
                            int constDays = round((end - begin) / 86400);
                            challenge_duration = String.valueOf(days);
                            constDuration = String.valueOf(constDays);
                        }

                        String stringSenderProgress[] = new String[progress.size()];
                        for (int j = 0; j < progress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(progress.get(j).toString());
                                long at = json.getLong("at");
                                stringSenderProgress[j] = String.valueOf(at);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        cv.put("name", challType); // Self-Improvement / Duel / Wake Up
                        cv.put("versus", versus); // if this is duel than versus = recipient / sender name
                        cv.put("wakeUpTime", challenge_detail); // our specific field for delete wakeUp (example: 1448);
                        cv.put("challengeName", challenge_name); // default 'challenge'. this column only for wake up time
                        cv.put("description", challenge_description); // smoking free life or wake up at 14:48
                        cv.put("duration", challenge_duration); // duration of challenge
                        cv.put("challenge_id", challenge_id); // in progress id
                        cv.put("status", challenge_status); // active or not
                        cv.put("updated", challenge_updated); // true or false (need to delete)
                        cv.put("myProgress", Arrays.toString(stringSenderProgress)); // last update time in millis
                        cv.put("constDuration", constDuration); // our constant value of challenge duration
                        cv.put("needsToCheck", needsToCheck); // method for check challenge for "needToCheck"
                        db.insert("myChallenges", null, cv); // db when we store all challenges and information about them


                    }
                    Log.d(TAG, "Generate onResponse: VSE OK");
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(getApplicationContext());
        loadData.loadUserProgressBarInfo();

    }

    // method which returns our last update (true or false);
    private String isUpdated(String challenge_id) {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String lastUpdate = "false";
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                // в методе "sendSingleForDuel мы засовываем challenge_id в колонку "challenge_id" в
                // таблице "updated", а тут мы ее проверяем. если она есть, то вернуть время когда
                // мы нажимали "дан" для дуелей, если её здесь нету, то возвращаем "false" - это для
                // wake-up и self-improvement челенджей.
                // Соответственно данные про update time для дуелей находятся в таблице "updated",
                // а для отсального в таблице "myChallenges".
                if (c.getString(colchallenge_id).equals(challenge_id)) {
                    lastUpdate = c.getString(c.getColumnIndex("updated"));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return lastUpdate;
    }


    private void notifyChallenges(Intent intent, String message) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(MyGcmListenerService.this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.duel_white)
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
                .setSmallIcon(R.drawable.friends)
                .setContentTitle("Champy")
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}