package com.example.ivan.champy_v2;

/**
 * Created by ivan on 25.03.16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {



        Intent i = new Intent();
        i.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.AlarmReceiverActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}