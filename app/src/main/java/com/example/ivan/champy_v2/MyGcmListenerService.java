package com.example.ivan.champy_v2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ivan.champy_v2.activity.FriendsActivity;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.activity.PendingDuelActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MyGcmListenerService extends GcmListenerService {

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
            HashMap<String, String> user = new HashMap<>();
            user = sessionManager.getUserDetails();
            String name = user.get("name");

            String message = data.getString("gcm.notification.body");
            String title = data.getString("gcm.notification.title");
            if (data != null) Log.d(TAG, "Bundle not null");
            Log.d(TAG, "From: " + from + " " + name);
            Log.d(TAG, "Message: " + message);

            if (from.startsWith("/topics/")) {
                // message received from some topic.
            } else {
                // normal downstream message.
            }

            // [START_EXCLUDE]
            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */

            if (!message.toLowerCase().contains(name.toLowerCase()))
                sendNotification(message, title);
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a item_friends_open notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title) {
        Log.d(TAG, title);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (title.equals("Friend request")) {
            intent = new Intent(this, FriendsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("friend_request", "true");
        } else if (title.equals("Congratulations")) {
            intent = new Intent(this, MainActivity.class);
        } else {
            refreshPendingDuels(message);
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.champy_icon)
                .setContentTitle("Champy")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void refreshPendingDuels(final String message) {
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
        final String id = user.get("id");
        String token = user.get("token");

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        //Log.i("stat", "Nam nado: " + id + " " + update + " " + token);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
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

                        //cv.clear();
                        if (challenge.getType().equals("567d51c48322f85870fd931b")) {
                            if (!challengeStatus.equals("started")) { //or .equals("pending");
                                if (id.equals(recipient.getId())) {
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
                    Intent intent = new Intent(MyGcmListenerService.this, PendingDuelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("refresh_duel", "true");
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyGcmListenerService.this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyGcmListenerService.this)
                            .setSmallIcon(R.drawable.champy_icon)
                            .setContentTitle("Champy")
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

}