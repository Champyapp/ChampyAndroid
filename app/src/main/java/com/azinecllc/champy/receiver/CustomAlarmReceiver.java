package com.azinecllc.champy.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AlarmReceiverActivity;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.azinecllc.champy.utils.Constants.oneDay;

public class CustomAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "CustomAlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(context);
        } catch (RuntimeException e) {
            Log.e(TAG, "onReceive: I Hate Facebook: " + e);
            e.printStackTrace();
        }

        final String progID = intent.getStringExtra("inProgressID");
        final int alarmID = intent.getIntExtra("alarmID", 0);
//        final int intHour   = intent.getIntExtra("alarmHour", 0);
//        final int intMin    = intent.getIntExtra("alarmMin",  0);

//        Calendar c = GregorianCalendar.getInstance();
//        long currentMidnight = System.currentTimeMillis() / 1000
//                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
//                - (c.get(Calendar.MINUTE) * 60)
//                - (c.get(Calendar.SECOND));
//
//        Date date = new Date();
//        date.setTime(((intMin * 60) + (intHour * 60 * 60) + currentMidnight) * 1000);
//        c.setTime(date);
//        if (System.currentTimeMillis() > c.getTimeInMillis()) {
//            c.add(Calendar.DAY_OF_YEAR, 1);
//        }
//
        Intent alarmIntent = new Intent(context, AlarmReceiverActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra("finalInProgressID", progID);
        alarmIntent.putExtra("finalAlarmID", alarmID);
        //alarmIntent.putExtra("finalNextAlarm", c.getTimeInMillis() / 1000);

        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isUserLoggedIn()) {
            sendNotification(context);
            //enableAlarmManager(context, alarmID, c.getTimeInMillis() / 1000);
            context.startActivity(alarmIntent);
        }

    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1337, notifyIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_wakeup_white)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentTitle("Champy")
                .setContentText("Wake up!")
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        notificationManager.notify(1337, builder.build());
    }

//    public void enableAlarmManager(Context context, int alarmID, long nextAlarm) {
//        Log.i(TAG, "enableAlarmManager: has setup new alarm at: " + nextAlarm);
//        Intent alarmIntent = new Intent(context, CustomAlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarm, AlarmManager.INTERVAL_DAY, pi);
//    }



}