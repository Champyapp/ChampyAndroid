package com.example.ivan.champy_v2.receiver;

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
        String alarmID      = intent.getStringExtra("alarmID");
//        int notifyInt = intent.getIntExtra("notifyIntent", 228);

        Log.d(TAG, "onReceive: AlarmReceiver:" + "\ninProgressId: " + inProgressId + "\nalarmID : " + alarmID);

        Intent alarmIntent = new Intent();
        alarmIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        alarmIntent.putExtra("finalInProgressID", inProgressId);
        alarmIntent.putExtra("finalAlarmID", alarmID);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        Intent notifyIntent = new Intent();
//        notifyIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.service.MyNotifyService");
//        notifyIntent.putExtra("notifyIntent", notifyInt);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isUserLoggedIn()) {
            context.startActivity(alarmIntent);
        } else {
            Log.i(TAG, "onReceive: AutoGiveUp. Reason: not logged in");
        }

    }
}