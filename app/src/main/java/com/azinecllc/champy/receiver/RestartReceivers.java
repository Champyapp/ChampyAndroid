package com.azinecllc.champy.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.azinecllc.champy.service.ResetService;

/**
 * Created by SashaKhyzhun on 2/22/17.
 */

public class RestartReceivers extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("CHAMPY RESTART RECEIVER", "onReceive: start");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent i = new Intent(context, ResetService.class);
            ComponentName service = context.startService(i);

            if (null == service) {
                // something really wrong here
                Log.e("CHAMPY RESTART RECEIVER", "onReceive: Could not start service");
            } else {
                Log.e("CHAMPY RESTART RECEIVER", "onReceive: Successfully started service");
            }

        }

    }

}
