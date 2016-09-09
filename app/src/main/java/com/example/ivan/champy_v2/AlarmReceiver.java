package com.example.ivan.champy_v2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.ivan.champy_v2.activity.AlarmReceiverActivity;
import com.example.ivan.champy_v2.activity.WakeUpActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        String inProgressId = intent.getStringExtra("inProgressId");
        int intentId = intent.getIntExtra("intentId", 0);

        Log.i(TAG, "onReceive | inProgressId: " + inProgressId);
        Log.i(TAG, "onReceive | intentId: " + intentId);

        Intent newIntent = new Intent();
        newIntent.setClassName("com.example.ivan.champy_v2", "com.example.ivan.champy_v2.activity.AlarmReceiverActivity");
        newIntent.putExtra("finalInProgressChallengeId", inProgressId);
        newIntent.putExtra("finalIntentId", intentId);

        /**
         * FLAG_ACTIVITY_NEW_TASK   - откроет wake up, но упадет, когда мы захочем зайти в мейн активити
         *                            можно по-сути этого избежать, просто "проиграв" челендж, а потом
         *                            загрузить карточки,когда юзер зайдет в приложение. для этого
         *                            надо убрать "релоад" с "гив апа" или сделать отдельный метод
         * FLAG_ACTIVITY_SINGLE_TOP - не сможет открыть wake up активити, когда срабатывает будильник
         * FLAG_ACTIVITY_CLEAR_TOP  - не сможет открыть wake up активити, когда срабатывает будильник
         */

        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.i(TAG, "onReceiveNewIntent | inProgressId: " + inProgressId);
        Log.i(TAG, "onReceiveNewIntent | intentId: " + intentId);

        context.startActivity(newIntent);
    }
}