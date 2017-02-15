package com.azinecllc.champy.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AlarmReceiverActivity;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.controller.DailyWakeUpController;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import static android.content.Context.NOTIFICATION_SERVICE;

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

        //final String progID = intent.getStringExtra("inProgressID");
        final int hour = intent.getIntExtra("hour", 0);
        final int min = intent.getIntExtra("min", 0);
        final int requestCode = intent.getIntExtra("requestCode", 0);
        System.out.println("Alarm | extras: hour" + hour + ", min: " + min + " (requestCode: " + requestCode + ")");

        Intent alarmIntent = new Intent(context, AlarmReceiverActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //alarmIntent.putExtra("finalInProgressID", progID);
        //alarmIntent.putExtra("finalAlarmID", alarmID);
        //alarmIntent.putExtra("finalNextAlarm", c.getTimeInMillis() / 1000);

        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isUserLoggedIn()) {
            sendNotification(context);

            DailyWakeUpController dwc = new DailyWakeUpController(context);
            new Handler().postDelayed(() -> dwc.enableDailyWakeUp(hour, min, requestCode), 1000);
            context.startActivity(alarmIntent);
        }

    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 8888, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_wakeup_white)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentTitle("Champy")
                .setContentText("Wake up!")
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        notificationManager.notify(8888, builder.build());
    }




}