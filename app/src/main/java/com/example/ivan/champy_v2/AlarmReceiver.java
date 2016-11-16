package com.example.ivan.champy_v2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.ivan.champy_v2.utils.SessionManager;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        String inProgressId = intent.getStringExtra("inProgressId");
        String intentId = intent.getStringExtra("intentId");
        int notifyInt = intent.getIntExtra("notifyIntent", 228);

        Intent newIntent = new Intent();
        newIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        newIntent.putExtra("finalInProgressChallengeId", inProgressId);
        newIntent.putExtra("finalIntentId", intentId);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent notifyIntent = new Intent();
        notifyIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.MyNotifyReceiver");
        notifyIntent.putExtra("notifyIntent", notifyInt);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isUserLoggedIn()) {
            context.startActivity(newIntent);
        } else {
            Log.i(TAG, "onReceive: AutoGiveUp. Reason: not logged in");
        }

    }
}