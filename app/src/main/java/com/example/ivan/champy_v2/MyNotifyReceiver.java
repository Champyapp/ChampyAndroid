package com.example.ivan.champy_v2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.example.ivan.champy_v2.activity.RoleControllerActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MyNotifyReceiver extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNM = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), RoleControllerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, 0);

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
