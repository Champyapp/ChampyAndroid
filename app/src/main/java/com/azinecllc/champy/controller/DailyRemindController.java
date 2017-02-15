package com.azinecllc.champy.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.azinecllc.champy.receiver.CustomNotifyReceiver;

import java.util.Calendar;
import java.util.Date;

public class DailyRemindController {

    private Context context;

    public DailyRemindController(Context context) { this.context = context; }


    public void enableDailyNotificationReminder() {
        Calendar myCalender = Calendar.getInstance();
        myCalender.set(Calendar.HOUR_OF_DAY, 11);
        myCalender.set(Calendar.MINUTE, 0);
        myCalender.set(Calendar.SECOND, 0);

        if (System.currentTimeMillis() > myCalender.getTimeInMillis()) {
            myCalender.add(Calendar.DAY_OF_YEAR, 1);
        }

        Date date = new Date(myCalender.getTimeInMillis());
        Date now = new Date(System.currentTimeMillis());
        System.out.println("Current time on device  : " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
        System.out.println("Enable Daily Reminder on: " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
        Intent notifyIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 9999, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pi);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pi);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 60 * 1000, pi);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 60 * 1000, pi);
        }
    }

    /*
    Value will be forced up to 60000 as of Android 5.1; don't rely on this to be exact less.
    Frequent alarms are bad for battery life.
    As of API 22, the AlarmManager will override near-future and high-frequency alarm requests,
    delaying the alarm at least 5 seconds into the future and ensuring that the repeat interval
    is at least 60 seconds.  If you really need to do work sooner than 5 seconds, post a delayed
    message or runnable to a Handler.
    */

    public void disableDailyNotificationReminder() {
        System.out.println("Disable Daily Reminder");
        Intent alarmIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 9999, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
