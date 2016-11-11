package com.example.ivan.champy_v2.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DailyRemind {

    private Context context;

    public DailyRemind(Context context) { this.context = context; }

    public void enableDailyRemind() {
        Intent myIntent = new Intent(context, MyNotifyService.class);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);

        Calendar myC = Calendar.getInstance();
        myC.set(Calendar.SECOND, 0);
        myC.set(Calendar.MINUTE, 48);
        myC.set(Calendar.HOUR, 11);
        myC.add(Calendar.DAY_OF_YEAR, 0);

        if (Calendar.getInstance().getTimeInMillis() > myC.getTimeInMillis()) myC.add(Calendar.DATE, 1);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myC.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void disableDailyReminder() {
        Intent alarmIntent = new Intent(context, MyNotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 228, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
