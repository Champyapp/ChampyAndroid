package com.azinecllc.champy.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.azinecllc.champy.receiver.CustomNotifyReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DailyRemindController {

    private Context context;

    public DailyRemindController(Context context) { this.context = context; }


    public void enableDailyNotificationReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 59);

        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent notifyIntent = new Intent(context, CustomNotifyReceiver.class);
        Log.d("DailyReminder", "enabled daily remind at: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        PendingIntent pi = PendingIntent.getBroadcast(context, 1337, notifyIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
    }


    public void disableDailyNotificationReminder() {
        Intent alarmIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1337, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }



}
