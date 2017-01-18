package com.azinecllc.champy.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.WakeUpActivity;

/**
 * Service for wake-up challenge.
 * I think this is useless.
 */
public class MyAlarmService extends IntentService {

    public MyAlarmService() {
        super("MyAlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Wake Up! lol");
    }

    private void sendNotification(String msg) {
        System.out.println("MyAlarmService.sendNotification() is working! hallelujah");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent goToWakeUp = new Intent(this, WakeUpActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, goToWakeUp, 0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.ic_champy_circle)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alarmNotificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(0, alarmNotificationBuilder.build());
    }

}