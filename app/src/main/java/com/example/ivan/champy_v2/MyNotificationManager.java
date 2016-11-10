package com.example.ivan.champy_v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class MyNotificationManager {

    private Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
    }


    public void activateDailyNotificationReminder() {
        Intent notifyIntent = new Intent(context, NotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, notifyIntent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void deactivateDailyNotificationReminder() {
        Intent alarmIntent = new Intent(context, NotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
