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


    public void enableDailyNotificationReminder(int hour) {
        Calendar myCalender = Calendar.getInstance();
        myCalender.set(Calendar.HOUR_OF_DAY, hour);
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

        int requestCode = (hour == 8) ? 8888 : 9999;
        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// this for android 4.4 and higher
            manager.setExact(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pi);
        } else {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }

    }

    public void disableDailyNotificationReminder(int hour) {
        System.out.println("Notification | Disable Daily Reminder at: " + hour + ":00:00");
        Intent alarmIntent = new Intent(context, CustomNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                9999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
