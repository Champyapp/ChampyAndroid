package com.example.ivan.champy_v2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.ivan.champy_v2.activity.AlarmReceiverActivity;
import com.example.ivan.champy_v2.activity.WakeUpActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        String challengeId = intent.getStringExtra("inProgressId");

        Intent newIntent = new Intent();
        newIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        newIntent.putExtra("finalInProgressChallengeId", challengeId);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(newIntent);
    }
}