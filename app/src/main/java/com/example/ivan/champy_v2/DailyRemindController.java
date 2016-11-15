package com.example.ivan.champy_v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DailyRemindController {

    private Context context;

    public DailyRemindController(Context context) { this.context = context; }

    public void enableDailyRemind() {
//        Intent myIntent = new Intent(context, MyNotifyReceiver.class);
//        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getService(context, 228, myIntent, 0);
//
//        Calendar myC = Calendar.getInstance();
//        myC.set(Calendar.SECOND, 0);
//        myC.set(Calendar.MINUTE, 0);
//        myC.set(Calendar.HOUR, 12);
//        myC.add(Calendar.DAY_OF_YEAR, 0);
//
//        //if (Calendar.getInstance().getTimeInMillis() > myC.getTimeInMillis()) myC.add(Calendar.DATE, 1);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myC.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        Intent notifyIntent = new Intent(context, MyNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, notifyIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Calendar twelveHours = Calendar.getInstance();
        Calendar currentCal  = Calendar.getInstance();
        twelveHours.set(Calendar.HOUR_OF_DAY, 13);
        twelveHours.set(Calendar.MINUTE, 18);
        twelveHours.set(Calendar.SECOND, 0);

        long twelveHoursTimeInMillis = twelveHours.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (twelveHoursTimeInMillis >= currentTime) {
            // set from next day
            twelveHours.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC, twelveHours.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

//         Intent myIntent = new Intent(context, MyNotifyReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
//        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
//
//        Calendar firingCal= Calendar.getInstance();
//        Calendar currentCal = Calendar.getInstance();
//
//        firingCal.set(Calendar.HOUR, 13); // At the hour you wanna fire
//        firingCal.set(Calendar.MINUTE, 0); // Particular minute
//        firingCal.set(Calendar.SECOND, 0); // particular second
//
//        long intendedTime = firingCal.getTimeInMillis();
//        long currentTime = currentCal.getTimeInMillis();
//
//        if(intendedTime >= currentTime){
//            // you can add buffer time too here to ignore some small differences in milliseconds
//            // set from today
//            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
//        } else{
//            // set from next day
//            // you might consider using calendar.add() for adding one day to the current day
//            firingCal.add(Calendar.DAY_OF_MONTH, 1);
//            intendedTime = firingCal.getTimeInMillis();
//
//            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
//        }
    }

    public void disableDailyReminder() {
        Intent alarmIntent = new Intent(context, MyNotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }



}
