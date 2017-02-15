package com.azinecllc.champy.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.FriendsActivity;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CustomNotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(context);
        } catch (RuntimeException e) {
            Log.e("CustomNotifyReceiver", "onReceive: I Hate Facebook: " + e);
            e.printStackTrace();
        }


        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isUserLoggedIn()) {
            sendNotification(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                DailyRemindController drc = new DailyRemindController(context);
                new Handler().postDelayed(drc::enableDailyNotificationReminder, 1000);
            }

        }

    }


    private void sendNotification(Context context) {
        Date date = new Date(System.currentTimeMillis());
        System.out.println("Received notification at: " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 9999, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_score_total)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentTitle("Champy")
                .setContentText("Time to improve yourself")
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        notificationManager.notify(9999, builder.build());
    }


}