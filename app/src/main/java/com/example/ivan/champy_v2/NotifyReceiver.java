package com.example.ivan.champy_v2;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifyReceiver extends BroadcastReceiver {

    public static final String TAG = "NotifyReceiver";
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        Intent service1 = new Intent(context, NotifyService.class);
        context.startService(service1);
    }
}
