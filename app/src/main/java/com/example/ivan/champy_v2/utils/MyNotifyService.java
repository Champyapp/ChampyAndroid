package com.example.ivan.champy_v2.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.activity.RoleControllerActivity;

public class MyNotifyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        /**
         * if (now != 12:00) {
         *    return;
         * }
         */

        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent myIntent = new Intent(this, RoleControllerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 228, myIntent, 0);

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.clock)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentTitle("Champy")
                .setContentText("Time to improve yourself")
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        mNM.notify(228, builder.build());

    }

}
