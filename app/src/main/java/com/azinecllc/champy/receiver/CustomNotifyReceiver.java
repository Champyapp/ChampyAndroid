package com.azinecllc.champy.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomNotifyReceiver extends WakefulBroadcastReceiver {

    public final String TAG = "CustomNotifyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
            Log.d(TAG, "onReceive: received new notification");
        } catch (RuntimeException e) {
            Log.e(TAG, "onReceive: I Hate Facebook: " + e);
            e.printStackTrace();
        }


        SessionManager sessionManager = SessionManager.getInstance(context);
        if (sessionManager.isUserLoggedIn()) {
            sendNotification();
            Log.d(TAG, "onReceive: send notification");
            DailyRemindController daily = new DailyRemindController(context);
            daily.enableDailyNotificationReminder();
        } else {
            Log.d(TAG, "onReceive: not logged in");
        }

    }


    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1337, notifyIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_score_total)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 1000, 1500)
                .setContentTitle("Champy")
                .setContentText("Time to improve yourself")
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        notificationManager.notify(1337, builder.build());
    }


}