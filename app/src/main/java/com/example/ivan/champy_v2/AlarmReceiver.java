package com.example.ivan.champy_v2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Intent i = new Intent();
        i.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Integer.parseInt(Intent.EXTRA_ALARM_COUNT));
        context.startActivity(i);
    }
}