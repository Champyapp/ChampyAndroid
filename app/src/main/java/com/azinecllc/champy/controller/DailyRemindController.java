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
        System.out.println("Notification | Current time on device  : " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
        System.out.println("Notification | Enable Daily Reminder on: " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
        Intent notifyIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 9999, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // this for android 4.4 and higher
            manager.setExact(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pi);
        } else {
            // This for old android devices (13%). Almost nobody uses it.
            // need to check, but really no devices with old versions

            // set once
            manager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pi);
            // set repeat
            manager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public void disableDailyNotificationReminder() {
        System.out.println("Notification | Disable Daily Reminder");
        Intent alarmIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 9999, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
