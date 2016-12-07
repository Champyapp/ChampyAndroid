package com.example.ivan.champy_v2.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (RuntimeException e) {
            Log.e(TAG, "onReceive: govno ebanoe: " + e);
            e.printStackTrace();
        }
        final String inProgressId = intent.getStringExtra("inProgressId");
        final String alarmID      = intent.getStringExtra("alarmID");

        Log.d(TAG, "onReceive: AlarmReceiver:" + "\ninProgressId: " + inProgressId + "\nalarmID : " + alarmID);

        Intent alarmIntent = new Intent();
        alarmIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        alarmIntent.putExtra("finalInProgressID", inProgressId);
        alarmIntent.putExtra("finalAlarmID", alarmID);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isUserLoggedIn()) {
            context.startActivity(alarmIntent);
        } else {
            Log.i(TAG, "onReceive: AutoGiveUp. Reason: not logged in");
        }

    }
}