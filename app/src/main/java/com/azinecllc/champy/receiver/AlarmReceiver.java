package com.azinecllc.champy.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (RuntimeException e) {
            Log.e(TAG, "onReceive: I Hate Facebook: " + e);
            e.printStackTrace();
        }
        final String inProgressId = intent.getStringExtra("inProgressID");
        final String alarmID      = intent.getStringExtra("alarmID");

        Log.d(TAG, "onReceive: AlarmReceiver:" + "\ninProgressId: " + inProgressId + "\nalarmID : " + alarmID);

        Intent alarmIntent = new Intent();
        alarmIntent.setClassName("com.azinecllc.champy", "com.azinecllc.champy.activity.AlarmReceiverActivity");
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