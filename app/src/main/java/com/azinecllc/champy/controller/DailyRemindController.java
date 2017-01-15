package com.azinecllc.champy.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.azinecllc.champy.receiver.CustomNotifyReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DailyRemindController {

    private Context context;

    public DailyRemindController(Context context) { this.context = context; }


    public void enableDailyNotificationReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        //System.out.println("System.currentTimeMillis: " + System.currentTimeMillis() + "\ncalender.currentInMillis: " + calendar.getTimeInMillis());
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            //System.out.println("System time > Needed time for remind");
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent notifyIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1337, notifyIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }


    public void disableDailyNotificationReminder() {
        Intent alarmIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1337, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }



}
