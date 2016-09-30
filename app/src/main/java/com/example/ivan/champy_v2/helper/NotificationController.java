package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.ivan.champy_v2.NotifyReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationController {

    private Context context;

    public NotificationController(Context context) {
        this.context = context;
    }


    public void activateDailyNotificationReminder() {
        Intent alarmIntent = new Intent(context, NotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void deactivateDailyNotificationReminder() {
        Intent alarmIntent = new Intent(context, NotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
