package com.azinecllc.champy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.azinecllc.champy.activity.AlarmReceiverActivity;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

public class CustomAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(context);
        } catch (RuntimeException e) {
            Log.e("Custom", "onReceive: I Hate Facebook: " + e);
            e.printStackTrace();
        }
        final String inProgressId = intent.getStringExtra("inProgressID");
        final String alarmID      = intent.getStringExtra("alarmID");
        final String details = intent.getStringExtra("details");

        //alarmIntent.setClassName("com.azinecllc.champy", "com.azinecllc.champy.activity.AlarmReceiverActivity");
        Intent alarmIntent = new Intent(context, AlarmReceiverActivity.class);
        alarmIntent.putExtra("finalInProgressID", inProgressId);
        alarmIntent.putExtra("finalAlarmID", alarmID);
        alarmIntent.putExtra("finalDetails", details);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isUserLoggedIn()) {
            context.startActivity(alarmIntent);
        }
    }


}